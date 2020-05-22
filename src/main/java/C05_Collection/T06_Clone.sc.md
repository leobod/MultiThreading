# Java源码分析--拷贝（浅/深）

## Clone

+ 浅克隆（Shadow Clone）
  + 原型对象中成员变量为**值**类型的属性都复制给克隆对象
  + 把原型对象中成员变量为引用类型的**引用地址**也复制给克隆对象
    + 原型对象中如果有成员变量为引用对象，则此引用对象的地址是共享给原型对象和克隆对象的。
+ 深克隆（Deep Clone）
  + 原型对象中的所有类型，无论是值类型还是引用类型，都复制一份给克隆对象
    + 深克隆会把原型对象和原型对象所引用的对象，都复制一份给克隆对象。

### SourceCode

在 Java 语言中要实现克隆则需要实现 Cloneable 接口，并重写 Object 类中的 clone() 方法

+ 对于所有对象来说
  + x.clone() !=x 应当返回 true，因为克隆对象与原对象不是同一个对象。
  + x.clone().getClass() == x.getClass() 应当返回 true，因为克隆对象与原对象的类型是一样的。
  + x.clone().equals(x) 应当返回 true，因为使用 equals 比较时，它们的值都是相同的。

```java
protected native Object clone() throws CloneNotSupportedException;

// clone() 是使用 native 修饰的本地方法，因此执行的性能会很高，并且它返回的类型为 Object
```



## 知识点

### 数组类型克隆

如果是数组类型，我们可以直接使用 Arrays.copyOf() 来实现克隆

```java
People[] o1 = {new People(1, "Java")};
People[] o2 = Arrays.copyOf(o1, o1.length);
```

### 深克隆实现方式汇总

+ 所有对象都实现克隆方法；
+ 通过构造方法实现深克隆；
+ 使用 JDK 自带的字节流实现深克隆；
+ 使用第三方工具实现深克隆，比如 Apache Commons Lang；
+ 使用 JSON 工具类实现深克隆，比如 Gson、FastJSON 等。

#### 所有对象都实现克隆

```java
public class CloneExample {
    public static void main(String[] args) throws CloneNotSupportedException {
          // 创建被赋值对象
          Address address = new Address(110, "北京");
          People p1 = new People(1, "Java", address);
          // 克隆 p1 对象
          People p2 = p1.clone();
          // 修改原型对象
          p1.getAddress().setCity("西安");
          // 输出 p1 和 p2 地址信息
          System.out.println("p1:" + p1.getAddress().getCity() +
                  " p2:" + p2.getAddress().getCity());
    }
    /**
     * 用户类
     */
    static class People implements Cloneable {
        private Integer id;
        private String name;
        private Address address;
        /**
         * 重写 clone 方法
         * @throws CloneNotSupportedException
         */
        @Override
        protected People clone() throws CloneNotSupportedException {
            People people = (People) super.clone();
            people.setAddress(this.address.clone()); // 引用类型克隆赋值
            return people;
        }
        // 忽略构造方法、set、get 方法
    }
    /**
     * 地址类
     */
    static class Address implements Cloneable {
        private Integer id;
        private String city;
        /**
         * 重写 clone 方法
         * @throws CloneNotSupportedException
         */
        @Override
        protected Address clone() throws CloneNotSupportedException {
            return (Address) super.clone();
        }
        // 忽略构造方法、set、get 方法
    }
}
```

#### 通过构造方法实现深克隆

```java
public class SecondExample {
    public static void main(String[] args) throws CloneNotSupportedException {
        // 创建对象
        Address address = new Address(110, "北京");
        People p1 = new People(1, "Java", address);

        // 调用构造函数克隆对象
        People p2 = new People(p1.getId(), p1.getName(),
                new Address(p1.getAddress().getId(), p1.getAddress().getCity()));

        // 修改原型对象
        p1.getAddress().setCity("西安");

        // 输出 p1 和 p2 地址信息
        System.out.println("p1:" + p1.getAddress().getCity() +
                " p2:" + p2.getAddress().getCity());
    }

    /**
     * 用户类
     */
    static class People {
        private Integer id;
        private String name;
        private Address address;
        // 忽略构造方法、set、get 方法
    }

    /**
     * 地址类
     */
    static class Address {
        private Integer id;
        private String city;
        // 忽略构造方法、set、get 方法
    }
}
```

#### 通过字节流实现深克隆

```java
import java.io.*;

public class ThirdExample {
    public static void main(String[] args) throws CloneNotSupportedException {
        // 创建对象
        Address address = new Address(110, "北京");
        People p1 = new People(1, "Java", address);

        // 通过字节流实现克隆
        People p2 = (People) StreamClone.clone(p1);

        // 修改原型对象
        p1.getAddress().setCity("西安");

        // 输出 p1 和 p2 地址信息
        System.out.println("p1:" + p1.getAddress().getCity() +
                " p2:" + p2.getAddress().getCity());
    }

    /**
     * 通过字节流实现克隆
     */
    static class StreamClone {
        public static <T extends Serializable> T clone(People obj) {
            T cloneObj = null;
            try {
                // 写入字节流
                ByteArrayOutputStream bo = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bo);
                oos.writeObject(obj);
                oos.close();
                // 分配内存,写入原始对象,生成新对象
              	//获取上面的输出字节流
                ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
                ObjectInputStream oi = new ObjectInputStream(bi);
                // 返回生成的新对象
                cloneObj = (T) oi.readObject();
                oi.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return cloneObj;
        }
    }

    /**
     * 用户类
     */
    static class People implements Serializable {
        private Integer id;
        private String name;
        private Address address;
        // 忽略构造方法、set、get 方法
    }

    /**
     * 地址类
     */
    static class Address implements Serializable {
        private Integer id;
        private String city;
        // 忽略构造方法、set、get 方法
    }
}
```

#### Apache Commons Lang 来实现深克隆

```java
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;

/**
 * 深克隆实现方式四：通过 apache.commons.lang 实现
 */
public class FourthExample {
    public static void main(String[] args) throws CloneNotSupportedException {
        // 创建对象
        Address address = new Address(110, "北京");
        People p1 = new People(1, "Java", address);

        // 调用 apache.commons.lang 克隆对象
        People p2 = (People) SerializationUtils.clone(p1);

        // 修改原型对象
        p1.getAddress().setCity("西安");

        // 输出 p1 和 p2 地址信息
        System.out.println("p1:" + p1.getAddress().getCity() +
                " p2:" + p2.getAddress().getCity());
    }

    /**
     * 用户类
     */
    static class People implements Serializable {
        private Integer id;
        private String name;
        private Address address;
        // 忽略构造方法、set、get 方法
    }

    /**
     * 地址类
     */
    static class Address implements Serializable {
        private Integer id;
        private String city;
        // 忽略构造方法、set、get 方法
    }
}
```

#### JSON 工具类实现深克隆

```java
import com.google.gson.Gson;

/**
 * 深克隆实现方式五：通过 JSON 工具实现
 */
public class FifthExample {
    public static void main(String[] args) throws CloneNotSupportedException {
        // 创建对象
        Address address = new Address(110, "北京");
        People p1 = new People(1, "Java", address);

        // 调用 Gson 克隆对象
        Gson gson = new Gson();
        People p2 = gson.fromJson(gson.toJson(p1), People.class);

        // 修改原型对象
        p1.getAddress().setCity("西安");

        // 输出 p1 和 p2 地址信息
        System.out.println("p1:" + p1.getAddress().getCity() +
                " p2:" + p2.getAddress().getCity());
    }

    /**
     * 用户类
     */
    static class People {
        private Integer id;
        private String name;
        private Address address;
        // 忽略构造方法、set、get 方法
    }

    /**
     * 地址类
     */
    static class Address {
        private Integer id;
        private String city;
        // 忽略构造方法、set、get 方法
    }
}
```

