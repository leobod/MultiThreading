package C04_LockSupportAndQ1Q2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * 使用Semaphore来解决Q1
 */

public class T08_Q1_Semaphore {
    volatile List lists = new ArrayList();

    public void add(Object o) {
        lists.add(o);
    }

    public int size() {
        return lists.size();
    }


    static Thread t1 = null, t2 = null;
    public static void main(String[] args) {
        T08_Q1_Semaphore t = new T08_Q1_Semaphore();

        Semaphore s = new Semaphore(1);

        t1 = new Thread(() -> {
            try {
                s.acquire();
                for (int i = 0; i < 5; ++i) {
                    t.add(new Object());
                    System.out.println("add : " + i);
                }
                s.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                t2.start();
                t2.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                s.acquire();
                for (int i = 5; i < 10; ++i) {
                    t.add(new Object());
                    System.out.println("add : " + i);
                }
                s.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t1");

        t2 = new Thread(() -> {
            try {
                s.acquire();
                System.out.println("t2 end");
                s.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t2");

        t1.start();
    }
}
