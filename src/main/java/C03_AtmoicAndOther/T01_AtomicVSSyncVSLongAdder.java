package C03_AtmoicAndOther;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * 比较Atomic与Synchronized与LongAdder的部分时候的性能
 *
 * 结果：
 * Atomic: 100000000 time 1706
 * Sync: 100000000 time 3545
 * LongAdder: 100000000 time 207
 */

public class T01_AtomicVSSyncVSLongAdder {
    static long count2 = 0L;
    static AtomicLong count1 = new AtomicLong(0L);
    static LongAdder count3 = new LongAdder();

    public static void main(String[] args) throws Exception {
        Thread[] threads = new Thread[1000];

        for (int i = 0; i < threads.length; ++i) {
            threads[i] = new Thread(() -> {
                for (int k = 0; k < 100000; ++k) {
                    count1.incrementAndGet();
                }
            });
        }
        long start = System.currentTimeMillis();
        for (Thread t : threads) { t.start(); }
        for (Thread t : threads) { t.join(); }
        long end = System.currentTimeMillis();
        System.out.println("Atomic: " + count1.get() + " time " + (end - start));

        Object lock = new Object();
        for (int i = 0; i < threads.length; ++i) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() { for (int k = 0; k < 100000; ++k) { synchronized (lock) { count2++; } }
                }
            });
        }
        start = System.currentTimeMillis();
        for (Thread t : threads) { t.start(); }
        for (Thread t : threads) { t.join(); }
        end = System.currentTimeMillis();
        System.out.println("Sync: " + count2 + " time " + (end - start));

        for (int i = 0; i < threads.length; ++i) {
            threads[i] = new Thread(() -> {
                for (int k = 0; k < 100000; ++k) { count3.increment(); }
            });
        }
        start = System.currentTimeMillis();
        for (Thread t : threads) { t.start(); }
        for (Thread t : threads) { t.join(); }
        end = System.currentTimeMillis();
        System.out.println("LongAdder: " + count3.longValue() + " time " + (end - start));
    }
}
