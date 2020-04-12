package C04_LockSupportAndQ1Q2;

import java.util.concurrent.locks.LockSupport;

/**
 * LockSupport 可以使用.park()进行线程阻塞
 *
 * 使用unpark(Thread_obj) 来对特定的线程解锁
 *
 * 多次的park使用主线程unpark，有可能还没有回到主线程执行，导致不能unpark(),t阻塞
 *
 * 要分开unpark
 *
 */

public class T02_LockSupportSupplement {

    public static void main(String[] args) {
        Thread t = new Thread(() -> {
            for (int i = 0; i < 10; ++i) {
                System.out.println(i);
                if (i == 5) {
                    System.out.println("Lock1");
                    LockSupport.park();
                    System.out.println("After UnLock1");
                }
                if (i == 8) {
                    System.out.println("Lock2");
                    LockSupport.park();
                    System.out.println("After UnLock2");
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        t.start();

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10; ++i) {
                System.out.println(i);
                if (i == 5) {
                    System.out.println("UnLock1");
                    LockSupport.unpark(t);
                }
                if (i == 8) {
                    System.out.println("UnLock2");
                    LockSupport.unpark(t);
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        t2.start();


//        System.out.println("UnLock1");
//        LockSupport.unpark(t);
//
//        System.out.println("UnLock2");
//        LockSupport.unpark(t);
    }
}
