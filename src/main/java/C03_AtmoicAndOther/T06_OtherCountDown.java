package C03_AtmoicAndOther;

import java.util.concurrent.CountDownLatch;

/**
 * CountDownLatch 指定门栓的长度/大小
 * 使用countDown()来减少长度或大小
 * 当latch的值为1时，门栓被打开
 *
 */

public class T06_OtherCountDown {

    private static void usingCountDownLatch() {
        Thread[] threads = new Thread[100];
        CountDownLatch latch = new CountDownLatch(threads.length);

        for (int i = 0; i < threads.length; ++i) {
            threads[i] = new Thread(() -> {
                int result = 0;
                for (int j = 0; j < 10000; ++j) {
                    result += j;
                }
                latch.countDown();
                System.out.println(Thread.currentThread().getName() + " : " + Integer.toString(result));
            }, Integer.toString(i));
        }

        for (int i = 0; i < threads.length; ++i) {
            threads[i].start();
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("End latch! ");
    }

    private static void usingJoin() {
        Thread[] threads = new Thread[100];

        for (int i = 0; i < threads.length; ++i) {
            threads[i] = new Thread(() -> {
                int result = 0;
                for (int j = 0; j < 10000; ++j) {
                    result += j;
                }
                System.out.println(Thread.currentThread().getName() + " : " + Integer.toString(result));
            }, Integer.toString(i));
        }

        for (int i = 0; i < threads.length; ++i) {
            threads[i].start();
        }

        for (int i = 0; i < threads.length; ++i) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("End Join");
    }


    public static void main(String[] args) {
        usingCountDownLatch();
        usingJoin();
    }
}
