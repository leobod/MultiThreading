package C04_LockSupportAndQ1Q2;

import java.util.concurrent.locks.LockSupport;

/**
 * LockSupport 可以使用.park()进行线程阻塞
 *
 * 使用unpark(Thread_obj) 来对特定的线程解锁
 *
 */

public class T01_WhatIsLockSupport {

    public static void main(String[] args) {
        Thread t = new Thread(() -> {
            for (int i = 0; i < 10; ++i) {
                System.out.println(i);
                if (i == 5) {
                    System.out.println("Lock1");
                    LockSupport.park();
                    System.out.println("After UnLock");
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        t.start();

        LockSupport.unpark(t);
        System.out.println("UnLock");

    }
}
