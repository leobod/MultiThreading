package C03_AtmoicAndOther;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用trylock进行尝试锁定
 * 对于synchronized来说，搞不定会阻塞，而ReentrantLock,你可以再见决定是否继续wait
 */

public class T03_TryLock {
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
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    void m2() {
        boolean locked = false;
        try {
            locked = lock.tryLock(5, TimeUnit.SECONDS);
            System.out.println(Thread.currentThread().getName() + "  m2...  " + locked);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (locked) {
                lock.unlock();
            }
        }
    }


    public static void main(String[] args) {
        T03_TryLock t = new T03_TryLock();
        new Thread(t::m1, "t1").start();
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(t::m2, "t2").start();
    }
}
