package C04_LockSupportAndQ1Q2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 使用CountDownLatch
 *
 * 注释掉 sleep 前后，运行结果 偏了一位， 而且latch打开后结果继续运行
 *
 * 不sleep 结果为 5 后打印 t2 end, 实时性有待改进
 *
 * sleep 后结果为 4 后打印 t2 end
 *
 */

public class T06_Q1_CountDownLatch {
    private volatile List lists = new ArrayList();

    public void add(Object o) {
        lists.add(o);
    }

    public int size() {
        return lists.size();
    }



    public static void main(String[] args) {
        T06_Q1_CountDownLatch t = new T06_Q1_CountDownLatch();

        CountDownLatch latch = new CountDownLatch(1);

        new Thread(() -> {
            if (t.size() != 5) {
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("t2 end");
        }, "t2").start();

        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            for (int i = 0; i < 10; ++i) {
                t.add(new Object());
                System.out.println("add " + i);

                if (t.size() == 5) {
                    latch.countDown();
                }

//                try {
//                    Thread.sleep(1);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        }, "t1").start();
    }
}
