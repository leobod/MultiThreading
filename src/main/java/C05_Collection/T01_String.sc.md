# Java源码分析--String

## String

```java
public final class String 
    implements java.io.Serializable, Comparable<String>, CharSequence {
    private final char value[];
    private int hash;
    
    // 构造函数
    public String(String original) {
        this.value = original.value;
        this.hash = original.hash;
    }
    public String(char value[]) {
        this.value = Arrays.copyof(value, value.length);
    }
    public String(StringBuffer buffer) {
        synchronized(buffer) {
            this.value = Arrays.copyof(buffer.getValue(), buffer.length());
        }
    }
    public String(StringBuilder builder) {
        this.value = Arrays.copyof(builder.getValue(), builder.length());
    }
    
    public boolean equals(Object anObject) {
        // 对象引用相同直接返回 true
        if (this == anObject) {
            return true;
        }
        // 判断需要对比的值是否为 String 类型，如果不是则直接返回 false
        if (anObject instanceof String) {
            String anotherString = (String)anObject;
            int n = value.length;
            if (n == anotherString.value.length) {
                // 把两个字符串都转换为 char 数组对比
                char v1[] = value;
                char v2[] = anotherString.value;
                int i = 0;
                // 循环比对两个字符串的每一个字符
                while (n-- != 0) {
                    // 如果其中有一个字符不相等就 true false，否则继续对比
                    if (v1[i] != v2[i])
                        return false;
                    i++;
                }
                return true;
            }
        }
        return false;
	}
    
    public int compareTo(String anotherString) {
        int len1 = value.length;
        int len2 = anotherString.value.length;
        // 获取到两个字符串长度最短的那个 int 值
        int lim = Math.min(len1, len2);
        char v1[] = value;
        char v2[] = anotherString.value;
        int k = 0;
        // 对比每一个字符
        while (k < lim) {
            char c1 = v1[k];
            char c2 = v2[k];
            if (c1 != c2) {
                // 有字符不相等就返回差值
                return c1 - c2;
            }
            k++;
        }
        return len1 - len2;
    }
    
}
```



### equals

String 类型重写了 Object 中的 equals() 方法，equals() 方法需要传递一个 Object 类型的参数值

比较时会先通过 instanceof 判断是否为 String 类型，

如果不是则会直接返回 false

```
Object oString = "123";
Object oInt = 123;
System.out.println(oString instanceof String); // 返回 true
System.out.println(oInt instanceof String); // 返回 false
```



### CompareTo

可以看出 compareTo() 方法和 equals() 方法都是用于比较两个字符串的，但它们有两点不同：

+ equals() 可以接收一个 Object 类型的参数，而 compareTo() 只能接收一个 String 类型的参数；
+ equals() 返回值为 Boolean，而 compareTo() 的返回值则为 int。



### 其他重要方法

indexOf()：查询字符串首次出现的下标位置
lastIndexOf()：查询字符串最后出现的下标位置
contains()：查询字符串中是否包含另一个字符串
toLowerCase()：把字符串全部转换成小写
toUpperCase()：把字符串全部转换成大写
length()：查询字符串的长度
trim()：去掉字符串首尾空格
replace()：替换字符串中的某些字符
split()：把字符串分割并返回字符串数组
join()：把字符串数组转为字符串



## 知识点

### == 和 equals 的区别

+ ==
  + == 对于基本数据类型来说，是用于比较"**值**"是否相等的
  + 而对于引用类型来说，是用于比较"**引用地址**"是否相同的。
+ equals
  + Object 中的 equals() 方法其实就是 ==
  + String 重写了 equals() 方法把它修改成比较两个字符串的值是否相等。



### 为什么 String 要用 final 修饰

+ 高效
  + String 是被 final 修饰的不可继承类
  + 使用 final，因为它能够缓存结果，当你在传参时不需要考虑谁会修改它的值
  + 如果是可变类的话，则有可能需要重新拷贝出来一个新值进行传参，这样在性能上就会有一定的损失。
+ 安全
  + 调用一些系统级操作指令之前，可能会有一系列校验
  + 如果是可变类的话，可能在你校验过后，它的内部的值又被改变了
  + 有可能会引起严重的系统崩溃问题
+ 只有字符串是不可变时，我们才能实现字符串常量池
  + 字符串常量池可以为我们缓存字符串，提高程序的运行效率



### String 和 StringBuilder、StringBuffer 的区别

因为 String 类型是不可变的，所以在字符串拼接的时候如果使用 String 的话性能会很低

因此我们就需要使用另一个数据类型 StringBuffer，

它提供了 append 和 insert 方法可用于字符串的拼接，

它使用 synchronized 来保证线程安全，如下源码所示：

```java
@Override
public synchronized StringBuffer append(Object obj) {
    toStringCache = null;
    super.append(String.valueOf(obj));
    return this;
}

@Override
public synchronized StringBuffer append(String str) {
    toStringCache = null;
    super.append(str);
    return this;
}
```

因为它使用了 synchronized 来保证线程安全，所以性能不是很高

于是在 JDK 1.5 就有了 StringBuilder，它同样提供了 append 和 insert 的拼接方法

但它没有使用 synchronized 来修饰，因此在性能上要优于 StringBuffer

所以在非并发操作的环境下可使用 StringBuilder 来进行字符串拼接。



### String 和 JVM

代码 "Ja"+"va" 被直接编译成了 "Java" ，因此 s1==s2 的结果才是 true

```java
String s1 = "Ja" + "va";
String s2 = "Java";
System.out.println(s1 == s2);
```



### String 和 intern()

```
String s = new String("1"); s.intern();
String s2 = "1"; System.out.println(s == s2);// false
String s3 = new String("1") + new String("1"); s3.intern();
String s4 = "11";
System.out.println(s3 == s4);// true

String s = newString("1")，生成了常量池中的“1” 和堆空间中的字符串对象。 
s.intern()，这一行的作用是 s 对象去常量池中寻找后发现"1"已经存在于常量池中了。 
String s2 = "1"，这行代码是生成一个 s2 的引用指向常量池中的“1”对象。
结果就是 s 和 s2 的引用地址明显不同。因此返回了 false。

String s3 = new String("1") + newString("1")，这行代码在字符串常量池中生成“1” ，并在堆 空间中生成 s3 引用指向的对象(内容为"11")。
注意此时常量池中是没有 “11”对象的。 
s3.intern()，这一行代码，是将 s3 中的“11”字符串放入 String 常量池中，此时常量池中不存在“11”字符串，
JDK1.6 的做法是直接在常量池中生成一个 "11" 的对象。
JDK1.7 中，常量池中不需要再存储一份对象了，可以直接存储堆中的引用。
这份引 用直接指向 s3 引用的对象，也就是说 s3.intern() ==s3 会返回 true。

String s4 = "11"， 这一行代码会直接去常量池中创建，但是发现已经有这个对象了，此时 也就是指向 s3 引用对象的一个引用。因此 s3 == s4 返回了 true。
```

