package C04_LockSupportAndQ1Q2;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 一个固定容器，拥有put get 方法，能够支持2个生产者与10个消费者
 * @param <T>
 */

public class T09_Q2<T> {
    final private LinkedList<T> lists = new LinkedList<T>();
    final private int MAX = 10;
    private int count = 0;
    private Lock lock = new ReentrantLock();
    private Condition producer = lock.newCondition();
    private Condition consumer = lock.newCondition();

    public void put(T t) {
        try {
            lock.lock();
            while (lists.size() == MAX) {
                producer.await();
            }
            lists.add(t);
            ++count;
            consumer.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public T get() {
        T t = null;
        try {
            lock.lock();
            while (lists.size() == 0) {
                consumer.await();
            }
            t = lists.removeFirst();
            count--;
            producer.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return t;
    }


    public static void main(String[] args) {
        T09_Q2<String> t = new T09_Q2<>();

        for (int i = 0; i < 10; ++i) {
            new Thread(() -> {
                for (int j = 0; j < 5; ++j) {
                    System.out.println(t.get());
                }
            }, "c-" + i).start();
        }

        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 2; ++i) {
            new Thread(() -> {
                for (int j = 0; j < 25; ++j) {
                    t.put(Thread.currentThread().getName() + " " + j);
                }
            }, "p-"+i).start();
        }
    }
}
