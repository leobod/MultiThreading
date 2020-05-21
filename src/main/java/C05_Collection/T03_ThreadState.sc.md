# Java源码分析--ThreadState

## State

### SourceCode

```java
public enum State {
    /**
     * 新建状态，线程被创建出来，但尚未启动时的线程状态
     */
    NEW,

    /**
     * 就绪状态，表示可以运行的线程状态，但它在排队等待来自操作系统的 CPU 资源
     */
    RUNNABLE,

    /**
     * 阻塞等待锁的线程状态，表示正在处于阻塞状态的线程
     * 正在等待监视器锁，比如等待执行 synchronized 代码块或者
     * 使用 synchronized 标记的方法
     */
    BLOCKED,

    /**
     * 等待状态，一个处于等待状态的线程正在等待另一个线程执行某个特定的动作。
     * 例如，一个线程调用了 Object.wait() 它在等待另一个线程调用
     * Object.notify() 或 Object.notifyAll()
     */
    WAITING,

    /**
     * 计时等待状态，和等待状态 (WAITING) 类似，只是多了超时时间，比如
     * 调用了有超时时间设置的方法 Object.wait(long timeout) 和 
     * Thread.join(long timeout) 就会进入此状态
     */
    TIMED_WAITING,

    /**
     * 终止状态，表示线程已经执行完成
     */
}

```

### 状态图

![state](./image/state.png)



### 线程的常用方法

#### join()

在一个线程中调用 other.join() ，这时候当前线程会让出执行权给 other 线程，直到 other 线程执行完或者过了超时时间之后再继续执行当前线程

```java
public final synchronized void join(long millis) throws InterruptedException {
    long base = System.currentTimeMillis();
    long now = 0;
    // 超时时间不能小于 0
    if (millis < 0) {
        throw new IllegalArgumentException("timeout value is negative");
    }
    // 等于 0 表示无限等待，直到线程执行完为之
    if (millis == 0) {
        // 判断子线程 (其他线程) 为活跃线程，则一直等待
        while (isAlive()) {
            wait(0);
        }
    } else {
        // 循环判断
        while (isAlive()) {
            long delay = millis - now;
            if (delay <= 0) {
                break;
            }
            wait(delay);
            now = System.currentTimeMillis() - base;
        }
    }
}
```

#### yield()

+ yield() 为本地方法
+ yield() 方法表示给线程调度器一个当前线程愿意出让 CPU 使用权的暗示，但是线程调度器可能会忽略这个暗示。

```java
public static native void yield();

public static void main(String[] args) throws InterruptedException {
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                System.out.println("线程：" +
                        Thread.currentThread().getName() + " I：" + i);
                if (i == 5) {
                    Thread.yield();
                }
            }
        }
    };
    Thread t1 = new Thread(runnable, "T1");
    Thread t2 = new Thread(runnable, "T2");
    t1.start();
    t2.start();
}
```



### 线程优先级

```java
public final void setPriority(int newPriority) {
    ThreadGroup g;
    checkAccess();
    // 先验证优先级的合理性
    if (newPriority > MAX_PRIORITY || newPriority < MIN_PRIORITY) {
        throw new IllegalArgumentException();
    }
    if((g = getThreadGroup()) != null) {
        // 优先级如果超过线程组的最高优先级，则把优先级设置为线程组的最高优先级
        if (newPriority > g.getMaxPriority()) {
            newPriority = g.getMaxPriority();
        }
        setPriority0(priority = newPriority);
    }
}
```





## 知识点

### BLOCKED（阻塞等待）和 WAITING（等待）有什么区别？

+ 虽然 BLOCKED 和 WAITING 都有等待的含义，但二者有着本质的区别
+ BLOCKED 可以理解为当前线程还处于活跃状态，只是在阻塞等待其他线程使用完某个锁资源
+ WAITING 则是因为自身调用了 Object.wait() 或着是 Thread.join() 又或者是 LockSupport.park() 而进入等待状态，只能等待其他线程执行某个特定的动作才能被继续唤醒



### start() 和 run() 的区别

+ start() 方法属于 Thread 自身的方法
  + 并且使用了 synchronized 来保证线程安全
  + 源码如下
+ run() 方法为 Runnable 的抽象方法，
  + 必须由调用类重写此方法，重写的 run() 方法其实就是此线程要执行的业务方法
+ 从执行的效果来说
  + start() 方法可以开启多线程，让线程从 NEW 状态转换成 RUNNABLE 状态
  +  run() 方法只是一个普通的方法。
+ 其次可调用的次数不同
  + start() 方法不能被多次调用，否则会抛出 java.lang.IllegalStateException
  + run() 方法可以进行多次调用，因为它只是一个普通的方法而已。

#### start源码

```java
public synchronized void start() {
    // 状态验证，不等于 NEW 的状态会抛出异常
    if (threadStatus != 0)
        throw new IllegalThreadStateException();
    // 通知线程组，此线程即将启动

    group.add(this);
    boolean started = false;
    try {
        start0();
        started = true;
    } finally {
        try {
            if (!started) {
                group.threadStartFailed(this);
            }
        } catch (Throwable ignore) {
            // 不处理任何异常，如果 start0 抛出异常，则它将被传递到调用堆栈上
        }
    }
}
```

#### run源码

```java
public class Thread implements Runnable {
 // 忽略其他方法......
  private Runnable target;
  @Override
  public void run() {
      if (target != null) {
          target.run();
      }
  }
}
@FunctionalInterface
public interface Runnable {
    public abstract void run();
}
```

