# Java源码分析--ReentrantLock

## ReentrantLock

synchronized 属于独占式悲观锁，是通过 JVM 隐式实现的，synchronized 只允许同一时刻只有一个线程操作资源。

在 Java 中每个对象都隐式包含一个 monitor（监视器）对象，加锁的过程其实就是竞争 monitor 的过程，当线程进入字节码 monitorenter 指令之后，线程将持有 monitor 对象，执行 monitorexit 时释放 monitor 对象，当其他线程没有拿到 monitor 对象时，则需要阻塞等待获取该对象。

ReentrantLock 是 Lock 的默认实现方式之一，它是基于 AQS（Abstract Queued Synchronizer，队列同步器）实现的，它默认是通过非公平锁实现的，在它的内部有一个 state 的状态字段用于表示锁是否被占用，如果是 0 则表示锁未被占用，此时线程就可以把 state 改为 1，并成功获得锁，而其他未获得锁的线程只能去排队等待获取锁资源。

### SourceCode

```java
// 构造
public ReentrantLock() {
    sync = new NonfairSync(); // 非公平锁
}
public ReentrantLock(boolean fair) {
    sync = fair ? new FairSync() : new NonfairSync();
}
```



### acquire

```java
public final void acquire(int arg) {
    if (!tryAcquire(arg) && acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
        selfInterrupt();
}
```

### unlock

锁的释放流程为，先调用 tryRelease 方法尝试释放锁，如果释放成功，则查看头结点的状态是否为 SIGNAL，如果是，则唤醒头结点的下个节点关联的线程；如果释放锁失败，则返回 false。

```java
public void unlock() {
    sync.release(1);
}
public final boolean release(int arg) {
    // 尝试释放锁
    if (tryRelease(arg)) {
        // 释放成功
        Node h = head;
        if (h != null && h.waitStatus != 0)
            unparkSuccessor(h);
        return true;
    }
    return false;
}
```

### tryRelease

```java
/**
 * 尝试释放当前线程占有的锁
 */
protected final boolean tryRelease(int releases) {
    int c = getState() - releases; // 释放锁后的状态，0 表示释放锁成功
    // 如果拥有锁的线程不是当前线程的话抛出异常
    if (Thread.currentThread() != getExclusiveOwnerThread())
        throw new IllegalMonitorStateException();
    boolean free = false;
    if (c == 0) { // 锁被成功释放
        free = true;
        setExclusiveOwnerThread(null); // 清空独占线程
    }
    setState(c); // 更新 state 值，0 表示为释放锁成功
    return free;
}

```



### NonfairSync

可以看出非公平锁比公平锁只是多了一行 compareAndSetState 方法，该方法是尝试将 state 值由 0 置换为 1，如果设置成功的话，则说明当前没有其他线程持有该锁，不用再去排队了，可直接占用该锁，否则，则需要通过 acquire 方法去排队。

#### lock

```java
final void lock() {
    if (compareAndSetState(0, 1))
        // 将当前线程设置为此锁的持有者
        setExclusiveOwnerThread(Thread.currentThread());
    else
        acquire(1);
}
```

#### tryAcquire 

+ tryAcquire 方法尝试获取锁，如果获取锁失败，则把它加入到阻塞队列中

```java
protected final boolean tryAcquire(int acquires) {
    final Thread current = Thread.currentThread();
    int c = getState();
    if (c == 0) {
        // 公平锁比非公平锁多了一行代码 !hasQueuedPredecessors() 
        if (compareAndSetState(0, acquires)) { //尝试获取锁
            setExclusiveOwnerThread(current); // 获取成功，标记被抢占
            return true;
        }
    }
    else if (current == getExclusiveOwnerThread()) {
        int nextc = c + acquires;
        if (nextc < 0)
            throw new Error("Maximum lock count exceeded");
        setState(nextc); // set state=state+1
        return true;
    }
    return false;
}
```





### FairSync

#### lock

```java
final void lock() {
    acquire(1);
}
```

#### tryAcquire 

对于此方法来说，公平锁比非公平锁只多一行代码 !hasQueuedPredecessors()，它用来查看队列中是否有比它等待时间更久的线程，如果没有，就尝试一下是否能获取到锁，如果获取成功，则标记为已经被占用。

如果获取锁失败，则调用 addWaiter 方法把线程包装成 Node 对象，同时放入到队列中，但 addWaiter 方法并不会尝试获取锁，acquireQueued 方法才会尝试获取锁，如果获取失败，则此节点会被挂起

```java
protected final boolean tryAcquire(int acquires) {
    final Thread current = Thread.currentThread();
    int c = getState();
    if (c == 0) {
   
        if (!hasQueuedPredecessors() &&
            compareAndSetState(0, acquires)) { //尝试获取锁
            setExclusiveOwnerThread(current); // 获取成功，标记被抢占
            return true;
        }
    }
    else if (current == getExclusiveOwnerThread()) {
        int nextc = c + acquires;
        if (nextc < 0)
            throw new Error("Maximum lock count exceeded");
        setState(nextc); // set state=state+1
        return true;
    }
    return false;
}
```

##### acquireQueued

该方法会使用 for(;;) 无限循环的方式来尝试获取锁，若获取失败，则调用 shouldParkAfterFailedAcquire 方法，尝试挂起当前线程

```java
/**
 * 队列中的线程尝试获取锁，失败则会被挂起
 */
final boolean acquireQueued(final Node node, int arg) {
    boolean failed = true; // 获取锁是否成功的状态标识
    try {
        boolean interrupted = false; // 线程是否被中断
        for (;;) {
            // 获取前一个节点（前驱节点）
            final Node p = node.predecessor();
            // 当前节点为头节点的下一个节点时，有权尝试获取锁
            if (p == head && tryAcquire(arg)) {
                setHead(node); // 获取成功，将当前节点设置为 head 节点
                p.next = null; // 原 head 节点出队，等待被 GC
                failed = false; // 获取成功
                return interrupted;
            }
            // 判断获取锁失败后是否可以挂起
            if (shouldParkAfterFailedAcquire(p, node) &&
                parkAndCheckInterrupt())
                // 线程若被中断，返回 true
                interrupted = true;
        }
    } finally {
        if (failed)
            cancelAcquire(node);
    }
}
```

##### shouldParkAfterFailedAcquire

```java
/**
 * 判断线程是否可以被挂起
 */
private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
    // 获得前驱节点的状态
    int ws = pred.waitStatus;
    // 前驱节点的状态为 SIGNAL，当前线程可以被挂起（阻塞）
    if (ws == Node.SIGNAL)
        return true;
    if (ws > 0) { 
        do {
        // 若前驱节点状态为 CANCELLED，那就一直往前找，直到找到一个正常等待的状态为止
            node.prev = pred = pred.prev;
        } while (pred.waitStatus > 0);
        // 并将当前节点排在它后边
        pred.next = node;
    } else {
        // 把前驱节点的状态修改为 SIGNAL
        compareAndSetWaitStatus(pred, ws, Node.SIGNAL);
    }
    return false;
}
```



### 图片说明

#### 说明1

![](./image/ThreadPoolExcutor.png)

#### 说明2

![](./image/Lock.png)



### 其他重要方法





## 知识点

### CAS

+ Compare and Swap，即比较再交换
  + jdk5增加了并发包java.util.concurrent.*,其下面的类使用CAS算法实现了区别于synchronouse同步锁的一种乐观锁
  + JDK 5之前Java语言是靠synchronized关键字保证同步的，这是一种独占锁，也是是悲观锁。
+ CAS算法理解
  + CAS是一种无锁算法，CAS有3个操作数，内存值V，旧的预期值A，要修改的新值B。
    + 当且仅当预期值A和内存值V相同时，将内存值V修改为B
+ 在原子类变量中，如java.util.concurrent.atomic中的AtomicXXX
  + 都使用了这些底层的JVM支持为数字类型的引用类型提供一种高效的CAS操作
  + `compareAndSet`
  + AtomicInteger.incrementAndGet的实现用了乐观锁技术，
    + 调用了类sun.misc.Unsafe库里面的 CAS算法，用CPU指令来实现无锁自增
  + AtomicInteger.incrementAndGet的自增比用synchronized的锁效率倍增。

### ABA问题

略