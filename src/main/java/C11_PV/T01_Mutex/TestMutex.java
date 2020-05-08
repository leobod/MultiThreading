package C11_PV.T01_Mutex;

import java.util.concurrent.Semaphore;

public class TestMutex {


    public static void main(String[] args) {
        Object mutex = new Object();
        final String[] str = {new String("Hello,")};

        Thread t1 = new Thread(() -> {
            synchronized (mutex) {
                str[0] += Thread.currentThread().getName();
                System.out.println(str[0]);
            }
        }, "t1");

        Thread t2 = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (mutex) {
                str[0] += Thread.currentThread().getName();
                System.out.println(str[0]);
            }
        }, "t2");

        Thread t3 = new Thread(() -> {
            synchronized (mutex) {
                str[0] += Thread.currentThread().getName();
                System.out.println(str[0]);
            }
        }, "t3");

        t1.start();
        t2.start();
        t3.start();
    }
}
