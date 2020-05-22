# Java--缓存与消息队列

## 缓存

+ 本地缓存是指程序级别的缓存组件
  + 它的特点是本地缓存和应用程序会运行在同一个进程中
  + 所以本地缓存的操作会非常快，因为在同一个进程内也意味着不会有网络上的延迟和开销。
  + 本地缓存适用于单节点非集群的应用场景
    + 它的优点是快，缺点是多程序无法共享缓存，
    + 比如分布式用户 Session 会话信息保存，由于每次用户访问的服务器可能是不同的
    + 如果不能共享缓存，那么就意味着每次的请求操作都有可能被系统阻止
    + 因为会话信息只保存在某一个服务器上，当请求没有被转发到这台存储了用户信息的服务器时，就会被认为是非登录的违规操作。
    + 除此之外，无法共享缓存可能会造成系统资源的浪费，这是因为每个系统都单独维护了一份属于自己的缓存，而同一份缓存有可能被多个系统单独进行存储，从而浪费了系统资源。

+ 分布式缓存是指将应用系统和缓存组件进行分离的缓存机制
  + 这样多个应用系统就可以共享一套缓存数据了
  + 它的特点是共享缓存服务和可集群部署，为缓存系统提供了高可用的运行环境，以及缓存共享的程序运行机制。

本地缓存可以使用 EhCache 和 Google 的 Guava 来实现，而分布式缓存可以使用 Redis 或 Memcached 来实现。



## 消息队列

消息队列的使用场景有很多，最常见的使用场景有以下几个。

+ 商品秒杀
+ 系统解耦
+ 日志记录



### 常用消息中间件 RabbitMQ

目前市面上比较常用的 MQ（Message Queue，消息队列）中间件有 RabbitMQ、Kafka、RocketMQ，如果是轻量级的消息队列可以使用 Redis 提供的消息队列

RabbitMQ 是一个老牌开源的消息中间件，它实现了标准的 AMQP（Advanced Message Queuing Protocol，高级消息队列协议）消息中间件，使用 Erlang 语言开发，支持集群部署，和多种客户端语言混合调用



RabbitMQ 具备以下几个优点：

+ 支持持久化，RabbitMQ 支持磁盘持久化功能，保证了消息不会丢失；
+ 高并发，RabbitMQ 使用了 Erlang 开发语言，
  + Erlang 是为电话交换机开发的语言，天生自带高并发光环和高可用特性；
+ 支持分布式集群，正是因为 Erlang 语言实现的，因此 RabbitMQ 集群部署也非常简单，只需要启动每个节点并使用 --link 把节点加入到集群中即可，并且 RabbitMQ 支持自动选主和自动容灾；
+ 支持多种语言，比如 Java、.NET、PHP、Python、JavaScript、Ruby、Go 等；
+ 支持消息确认，支持消息消费确认（ack）保证了每条消息可以被正常消费；
+ 它支持很多插件，比如网页控制台消息管理插件、消息延迟插件等，RabbitMQ 的插件很多并且使用都很方便。



RabbitMQ 的消息类型，分为以下四种：

+ direct（默认类型）模式，
  + 为一对一的发送方式，也就是一条消息只会发送给一个消费者；
+ headers 模式，
  + 允许你匹配消息的 header 而非路由键（RoutingKey），除此之外 headers 和 direct 的使用完全一致，但因为 headers 匹配的性能很差，几乎不会被用到；
+ fanout 模式，
  + 为多播的方式，会把一个消息分发给所有的订阅者；
+ topic 模式，
  + 为主题订阅模式，允许使用通配符（#、*）匹配一个或者多个消息，我可以使用“cn.mq.#”匹配到多个前缀是“cn.mq.xxx”的消息，比如可以匹配到“cn.mq.rabbit”、“cn.mq.kafka”等消息。



## 自定义消息队列

+ 双端队列（Deque），是 Queue 的子类也是 Queue 的补充类，头部和尾部都支持元素插入和获取
+ 阻塞队列指的是在元素操作时（添加或删除），如果没有成功，会阻塞等待执行，比如当添加元素时，如果队列元素已满，队列则会阻塞等待直到有空位时再插入
+ 非阻塞队列，和阻塞队列相反，它会直接返回操作的结果，而非阻塞等待操作，双端队列也属于非阻塞队列。

### 实现1

```java
import java.util.LinkedList;
import java.util.Queue;

public class CustomQueue {
    // 定义消息队列
    private static Queue<String> queue = new LinkedList<>();

    public static void main(String[] args) {
        producer(); // 调用生产者
        consumer(); // 调用消费者
    }

    // 生产者
    public static void producer() {
        // 添加消息
        queue.add("first message.");
        queue.add("second message.");
        queue.add("third message.");
    }

    // 消费者
    public static void consumer() {
        while (!queue.isEmpty()) {
            // 消费消息
            System.out.println(queue.poll());
        }
    }
}
```



### 实现2--(延迟队列)

```java
import lombok.Getter;
import lombok.Setter;

import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 自定义延迟队列
 */
public class CustomDelayQueue {
    // 延迟消息队列
    private static DelayQueue delayQueue = new DelayQueue();

    public static void main(String[] args) throws InterruptedException {
        producer(); // 调用生产者
        consumer(); // 调用消费者
    }

    // 生产者
    public static void producer() {
        // 添加消息
        delayQueue.put(new MyDelay(1000, "消息1"));
        delayQueue.put(new MyDelay(3000, "消息2"));
    }

    // 消费者
    public static void consumer() throws InterruptedException {
        System.out.println("开始执行时间：" +
                DateFormat.getDateTimeInstance().format(new Date()));
        while (!delayQueue.isEmpty()) {
            System.out.println(delayQueue.take());
        }
        System.out.println("结束执行时间：" +
                DateFormat.getDateTimeInstance().format(new Date()));
    }

    /**
     * 自定义延迟队列
     */
    static class MyDelay implements Delayed {
        // 延迟截止时间（单位：毫秒）
        long delayTime = System.currentTimeMillis();

        // 借助 lombok 实现
        @Getter
        @Setter
        private String msg;

        /**
         * 初始化
         * @param delayTime 设置延迟执行时间
         * @param msg       执行的消息
         */
        public MyDelay(long delayTime, String msg) {
            this.delayTime = (this.delayTime + delayTime);
            this.msg = msg;
        }

        // 获取剩余时间
        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(delayTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }

        // 队列里元素的排序依据
        @Override
        public int compareTo(Delayed o) {
            if (this.getDelay(TimeUnit.MILLISECONDS) > o.getDelay(TimeUnit.MILLISECONDS)) {
                return 1;
            } else if (this.getDelay(TimeUnit.MILLISECONDS) < o.getDelay(TimeUnit.MILLISECONDS)) {
                return -1;
            } else {
                return 0;
            }
        }

        @Override
        public String toString() {
            return this.msg;
        }
    }
}
```

