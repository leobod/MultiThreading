package C04_LockSupportAndQ1Q2;

import java.util.ArrayList;
import java.util.List;


/**
 * 使用volatile与synchronized
 *
 * volatile保证可见性
 * synchronized来加锁
 *
 * 先启动 t2用于监听，在启动 t1
 * 结果依然不理想
 *
 * 分析，t1中的lock.notify(),只唤醒t2，并没有释放lock对象的锁
 * 所以t2，依然是t1结束后在运行。
 *
 */

public class T04_Q1_Volatile {
    private volatile List lists = new ArrayList();

    public void add(Object o) {
        lists.add(o);
    }

    public int size() {
        return lists.size();
    }



    public static void main(String[] args) {
        T04_Q1_Volatile t = new T04_Q1_Volatile();

        final Object lock = new Object();

        new Thread(() -> {
            synchronized (lock) {
                if (t.size() != 5) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("t2 end");
            }
        }, "t2").start();

        new Thread(() -> {
            synchronized (lock) {
                for (int i = 0; i < 10; ++i) {
                    t.add(new Object());
                    System.out.println("add " + i);

                    if (t.size() == 5) {
                        lock.notify();
                    }

                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "t1").start();


    }

}
