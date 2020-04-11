package C03_AtmoicAndOther;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * CyclicBarrier，又称为循环栅栏
 * 满足第一个参数时，才执行第二个参数
 *
 */

public class T07_CyclicBarrier {

    public static void main(String[] args) {
        CyclicBarrier barrier = new CyclicBarrier(20, () -> {
            System.out.println("条件全部符合，开始执行!");
        });

        for (int i=0; i<100; ++i) {
            new Thread(() -> {
                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
