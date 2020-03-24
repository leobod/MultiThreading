package C01_BasicConcept;

/**
 * 学习sleep、yield、join方法
 *
 * Thread.sleep()  TimeUnit.Milliseconds.sleep()  单位毫秒
 * 意思就是说，让线程休眠指定时间，休眠结束后运行
 *
 * Thread.yield()
 * 意思就是说，让线程回到等待的队列，让线程重新去系统的调度里面来获取执行权限
 *
 * Thread.join()
 * 意思就是说，当有2个以上的线程的时候
 * 假设t1,t2，当在t2的某一处调用t1.join(),则t2要等t1运行结束后才能运行
 */

public class T03_LearSleepYieldJoin {
    static void testSleep() {
        new Thread(() -> {
            for (int i = 0; i < 100; ++i) {
                System.out.println("A" + i);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    static void testYield() {
        new Thread(() -> {
            for (int i = 0; i < 100; ++i) {
                System.out.println("A" + i);
                if (i % 10 == 0) {
                    Thread.yield();
                }
            }
        }).start();
        new Thread(() -> {
            for (int i = 0; i < 100; ++i) {
                System.out.println("B" + i);
                if (i % 10 == 0) {
                    Thread.yield();
                }
            }
        }).start();
    }

    static void testJoin() {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 100; ++i) {
                System.out.println("A" + i);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread t2 = new Thread(() -> {
            try {
                t1.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < 100; ++i) {
                System.out.println("B" + i);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.start();
        t2.start();
    }

    public static void main(String[] args) {
//        testSleep();
        testYield();
//        testJoin();
    }
}
