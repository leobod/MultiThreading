package C03_AtmoicAndOther;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 公平锁： 谁等在前面就先让谁执行
 *
 * 非公平锁，存在线程来了就抢，而且有可能抢到。
 */

public class T05_FairOrUnfairLock {
    private static ReentrantLock lock = new ReentrantLock(true);  // 公平锁

    public void m() {
        for (int i = 0; i < 100; ++i) {
            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " lock");
            } finally {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) {
        T05_FairOrUnfairLock t = new T05_FairOrUnfairLock();

        Thread t1 = new Thread(t::m, "t1");
        Thread t2 = new Thread(t::m, "t2");

        t1.start();
        t2.start();
    }
}
