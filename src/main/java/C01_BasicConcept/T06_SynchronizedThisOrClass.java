package C01_BasicConcept;

/**
 * 使用synchronized放在this和方法上，一定程度等价
 *
 * 使用synchronized来给static方法加锁
 */

public class T06_SynchronizedThisOrClass {
    static class T {
        private int count = 10;

        public void m1() {
            synchronized (this) {
                count--;
                System.out.println(Thread.currentThread().getName() + "count = " + count);
            }
        }

        public synchronized void m2() {
            count--;
            System.out.println(Thread.currentThread().getName() + "count = " + count);
        }
    }

    static class T2 {
        private static int count = 10;

        public synchronized static void m() {
            synchronized (T2.class) {
                count--;
                System.out.println(Thread.currentThread().getName() + "count = " + count);
            }
        }
    }

    // 验证m1与m2方法是否同样具有资源锁定效果
    static void testM1M2() {
        T t = new T();
        new Thread(() -> {
            System.out.println("TT01");
            for (int i = 0; i < 3; i++) {
                t.m1();
            }
        }).start();
        new Thread(() -> {
            System.out.println("TT02");
            for (int i = 0; i < 3; i++) {
                t.m2();
            }
        }).start();
    }

    // 验证锁定static类的效果
    static void testM() {
        T2 t = new T2();
        new Thread(() -> {
            System.out.println("TT01");
            for (int i = 0; i < 3; i++) {
                t.m();
            }
        }).start();
        new Thread(() -> {
            System.out.println("TT02");
            for (int i = 0; i < 3; i++) {
                t.m();
            }
        }).start();
    }

    public static void main(String[] args) {
//        testM1M2();
        testM();
    }
}
