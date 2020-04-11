package C03_AtmoicAndOther;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ReadWriteLock，共享锁，排他锁
 *
 * ReadWriteLock一般比Lock性能会好
 *
 * ReentrantReadWriteLock
 * ReentrantLock
 */

public class T09_ReadWriteLock {
    private static int value;

    static ReadWriteLock lock = new ReentrantReadWriteLock();
    static Lock readLock = lock.readLock();
    static Lock writeLock = lock.writeLock();

    public static void read(Lock lock) {
        try {
            lock.lock();
            Thread.sleep(1000);
            System.out.println(value);
            System.out.println("Read Over.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public static void write(Lock lock, int v) {
        try {
            lock.lock();
            Thread.sleep(1000);
            value = v;
            System.out.println("Write Over");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }


    public static void main(String[] args) {
        Runnable readR = () -> read(readLock);
        Runnable writeR = () -> write(writeLock, 2);

        for (int i=0; i<9; ++i) {
            new Thread(readR).start();
        }
        for (int i=0; i<2; ++i) {
            new Thread(writeR).start();
        }
        for (int i=0; i<9; ++i) {
            new Thread(readR).start();
        }

    }
}
