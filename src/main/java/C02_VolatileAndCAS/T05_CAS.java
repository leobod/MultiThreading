package C02_VolatileAndCAS;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 要么使用volatile与synchronized来保证一致
 *
 * 要么使用AtomicInteger,Atomic使用的都是CAS
 *
 * 原理  v=0 ，excepted=0, newValue=1
 *  如果 读取到 excepted == v，则本次操作线程安全
 *  如果 读取到 excepted != v，则本次线程不安全
 *
 *  CAS 可能会存在ABA问题，
 *      也就是是说 一个线程修改了v,后来v又改了回来
 *      导致 excepted == v,使得误以为线程安全
 *
 *  解决办法，加版本号，每次修改版本号+1， 比对时，还要比对版本号
 */

public class T05_CAS {
    AtomicInteger count = new AtomicInteger(0);

    void m() {
        for (int i = 0; i < 10000; ++i) {
            count.incrementAndGet();
        }
    }

    public static void main(String[] args) {
        T05_CAS t = new T05_CAS();

        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < 10; ++i) {
            threads.add(new Thread(t::m, "Thread-" + i));
        }

        threads.forEach((o) -> o.start());

        threads.forEach((o) -> {
            try {
                o.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        System.out.println(t.count);

    }
}
