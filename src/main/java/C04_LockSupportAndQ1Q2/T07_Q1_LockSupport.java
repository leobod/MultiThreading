package C04_LockSupportAndQ1Q2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.LockSupport;

/**
 * 使用LockSupport来解决Q1
 *
 * 貌似 刻意化， 增加了复杂度
 *
 */

public class T07_Q1_LockSupport {
    volatile List lists = new ArrayList();

    public void add(Object o) {
        lists.add(o);
    }

    public int size() {
        return lists.size();
    }


    static Thread t1 = null, t2 = null;

    public static void main(String[] args) {
        T07_Q1_LockSupport t = new T07_Q1_LockSupport();

        CountDownLatch latch = new CountDownLatch(1);

        t2 = new Thread(() -> {
            if (t.size() != 5) {
                LockSupport.park();
            }
            System.out.println("t2 end");
            LockSupport.unpark(t1);
        }, "t2");

        t2.start();

        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        t1 = new Thread(() -> {
            for (int i = 0; i < 10; ++i) {
                t.add(new Object());
                System.out.println("add " + i);

                if (t.size() == 5) {
                    LockSupport.unpark(t2);
                    LockSupport.park();
                }

                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "t1");

        t1.start();
    }
}
