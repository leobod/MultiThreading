package C03_AtmoicAndOther;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 可重入锁
 * synchronized具有可重入性，自动加锁，解锁
 *
 * ReentrantLock手动加锁，也需要手动解锁
 *
 * ReentrantLock使用CAS                    T02
 * trylock自己控制，如果锁不住该怎么处理       T03
 * lockInterruptibly,锁住或等待时，可以被打断 T04
 * 自己可以指定，公平锁或非公平锁              T05
 */

public class T02_ReentrantLock {
    Lock lock = new ReentrantLock();

    void m1() {
        try {
            lock.lock();
            for (int i = 0; i < 10; ++i) {
                Thread.sleep(1);
                System.out.println(i);
            }
            this.m2();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    void m2() {
        try {
            lock.lock();
            System.out.println(Thread.currentThread().getName() + "m2....");
        } finally {
            lock.unlock();
        }
    }


    public static void main(String[] args) {
        T02_ReentrantLock t = new T02_ReentrantLock();
        new Thread(t::m1, "t1").start();

        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(t::m2, "t2").start();
    }
}
