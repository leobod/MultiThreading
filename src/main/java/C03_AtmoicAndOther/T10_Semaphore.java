package C03_AtmoicAndOther;

import java.util.concurrent.Semaphore;

/**
 * 限流
 *
 * CountDownLatch,CyclicBarrier,Phaser,ReadWriteLock,Semaphore，Exchanger都是使用AQS实现
 */

public class T10_Semaphore {
    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(2, true);

        new Thread(() -> {
            try {
                semaphore.acquire();
                System.out.println("T1 Running ... ");
                Thread.sleep(200);
                System.out.println("T1 Running ... ");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                semaphore.release();
            }
        }).start();

        new Thread(() -> {
            try {
                semaphore.acquire();
                System.out.println("T2 Running ... ");
                Thread.sleep(200);
                System.out.println("T2 Running ... ");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                semaphore.release();
            }
        }).start();

        new Thread(() -> {
            try {
                semaphore.acquire();
                System.out.println("T3 Running ... ");
                Thread.sleep(200);
                System.out.println("T3 Running ... ");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                semaphore.release();
            }
        }).start();
    }
}
