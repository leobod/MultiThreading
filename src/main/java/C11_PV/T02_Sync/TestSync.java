package C11_PV.T02_Sync;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TestSync {
    private int number = 0;
    private Lock lock = new ReentrantLock();
    private Condition producer = lock.newCondition();
    private Condition consumer = lock.newCondition();
    Object mutex = new Object();

    public void p1() {
        try {
            lock.lock();
            synchronized (mutex) {
                number++;
                System.out.println(" 存放 : ");
            }
            consumer.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void p2() {
        try {
            lock.lock();
            consumer.await();
            synchronized (mutex) {
                number--;
                System.out.println(" 取出 : ");
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        TestSync sync = new TestSync();

        Thread t1 = new Thread(sync::p1);

        Thread t2 = new Thread(sync::p2);

        t2.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        t1.start();

    }
}
