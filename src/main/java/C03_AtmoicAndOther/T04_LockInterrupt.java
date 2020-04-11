package C03_AtmoicAndOther;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用lock的方式 ，线程2会等线程1，线程2不会被打断
 *
 * 使用lockInterruptibly的方式，线程2可以使用t2.interrupt()来打断
 */

public class T04_LockInterrupt {
    Lock lock = new ReentrantLock();

    void m1() {
        try {
            lock.lock();
            for (int i = 0; i < 3; ++i) {
                Thread.sleep(1);
                System.out.println(i);
                m2();
            }
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " Interrupt");
        } finally {
                lock.unlock();
        }
    }

    void m2() {
        try {
            lock.lockInterruptibly();
            System.out.println(Thread.currentThread().getName() + " start");
            Thread.sleep(5);
            System.out.println(Thread.currentThread().getName() + " end");
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " Interrupt");
        } finally {
            lock.unlock();
        }
    }


    public static void main(String[] args) {
        T04_LockInterrupt t = new T04_LockInterrupt();
        Thread t1 = new Thread(t::m1, "t1");
        Thread t2 = new Thread(t::m2, "t2");

        t1.start();
        t2.start();

        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t2.interrupt();

    }
}
