package C04_LockSupportAndQ1Q2;

import java.util.ArrayList;
import java.util.List;

/**
 * 使用volatile与改进的synchronized操作
 *
 * volatile保证可见性
 * synchronized来加锁，与唤醒
 *
 * 先启动 t2用于监听，在启动 t1
 *
 * 改进，执行到指定条件后，不仅t1要notifyt2,还要wait,来放开锁
 *
 */

public class T05_Q1_ModifySynchronized {
    private volatile List lists = new ArrayList();

    public void add(Object o) {
        lists.add(o);
    }

    public int size() {
        return lists.size();
    }



    public static void main(String[] args) {
        T05_Q1_ModifySynchronized t = new T05_Q1_ModifySynchronized();

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
            lock.notify();
        }, "t2").start();

        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            synchronized (lock) {
                for (int i = 0; i < 10; ++i) {
                    t.add(new Object());
                    System.out.println("add " + i);

                    if (t.size() == 5) {
                        lock.notify();
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
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
