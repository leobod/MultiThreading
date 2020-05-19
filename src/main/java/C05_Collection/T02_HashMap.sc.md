# Java源码分析--HashMap

## Node

```java
static class Node<K,V> implements Map.Entry<K,V> {
    final int hash;
    final K key;
    V value;
    Node<K,V> next;

    Node(int hash, K key, V value, Node<K,V> next) {
        this.hash = hash;
        this.key = key;
        this.value = value;
        this.next = next;
    }

    public final K getKey()        { return key; }
    public final V getValue()      { return value; }
    public final String toString() { return key + "=" + value; }

    public final int hashCode() {
        return Objects.hashCode(key) ^ Objects.hashCode(value);
    }

    public final V setValue(V newValue) {
        V oldValue = value;
        value = newValue;
        return oldValue;
    }

    public final boolean equals(Object o) {
        if (o == this)
            return true;
        if (o instanceof Map.Entry) {
            Map.Entry<?,?> e = (Map.Entry<?,?>)o;
            if (Objects.equals(key, e.getKey()) &&
                Objects.equals(value, e.getValue()))
                return true;
        }
        return false;
    }
}
```



## HashMap

在 JDK 1.7 中 HashMap 是以数组加链表的形式组成的

JDK 1.8 之后新增了红黑树的组成结构，当链表大于 8 并且容量大于 64 时，链表结构会转换成红黑树结构

```java
public final class HashMap {
    
    // HashMap 初始化长度
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16
    // HashMap 最大长度
    static final int MAXIMUM_CAPACITY = 1 << 30; // 1073741824
    // 默认的加载因子 (扩容因子)
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    // 当链表长度大于此值且容量大于 64 时
    static final int TREEIFY_THRESHOLD = 8;
    // 转换链表的临界值，当元素小于此值时，会将红黑树结构转换成链表结构
    static final int UNTREEIFY_THRESHOLD = 6;
    // 最小树容量
    // 最小树形化容量阈值：即 当哈希表中的容量 > 该值时，才允许树形化链表 （即 将链表 转换成红黑树）
	// 否则，若桶内元素太多时，则直接扩容，而不是树形化
	// 为了避免进行扩容、树形化选择的冲突，这个值不能小于 4 * TREEIFY_THRESHOLD
    static final int MIN_TREEIFY_CAPACITY = 64;
    
    
    /** 构造方法 1 */
    public HashMap() {
        this.loadFactor = DEFAULT_LOAD_FACTOR; // all other fields defaulted
    }
    public HashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }
    public HashMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " +
                                               initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " +
                                               loadFactor);
        this.loadFactor = loadFactor;
        this.threshold = tableSizeFor(initialCapacity);
    }
    public HashMap(Map<? extends K, ? extends V> m) {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        putMapEntries(m, false);
    }
    
}
```



### 其他重要方法





## 知识点

### HashMap特点

+ HashMap是基于哈希表的Map接口实现。

+ HashMap底层采用的是Entry数组和链表实现。

+ HashMap是采用key-value形式存储，
  + 其中key是可以允许为null但是只能是一个，并且key不允许重复(如果重复则新值覆盖旧值)。

+ HashMap是线程不安全的。
+ HashMap存入的顺序和遍历的顺序有可能是不一致的。
+ HashMap保存数据的时候通过计算key的hash值来去决定存储的位置。



### 什么是加载因子？加载因子为什么是 0.75？

+ 加载因子也叫扩容因子或负载因子，用来判断什么时候进行扩容的
  + 假如加载因子是 0.5，
  + HashMap 的初始化容量是 16，
  + 那么当 HashMap 中有 16*0.5=8 个元素时，HashMap 就会进行扩容。
+ 那加载因子为什么是 0.75 而不是 0.5 或者 1.0 呢？
  + 当加载因子设置比较大的时候，
    + 扩容的门槛就被提高了，扩容发生的频率比较低
    + 占用的空间会比较小，但此时发生 Hash 冲突的几率就会提升
    + 因此需要更复杂的数据结构来存储元素，这样对元素的操作时间就会增加，运行效率也会因此降低
  + 当加载因子值比较小的时候
    + 扩容的门槛会比较低，因此会占用更多的空间，此时元素的存储就比较稀疏
    + 发生哈希冲突的可能性就比较小，因此操作性能会比较高。
+ 0.75出于容量和性能之间平衡的结果



