package C01_BasicConcept;

/**
 * 线程原子性，可见性
 */

public class T07_ThreadVisable {
    /**
     * 使用volatile可以保证，可见性
     * 也就是说 count 变化的时候，对于其他线程可见
     */
    static class T implements Runnable {
        private volatile int count = 100;

        @Override
        public void run() {
            count--;
            System.out.println(Thread.currentThread().getName() + " count = " + count);
        }
    }
    static void testVolatile() {
        T t = new T();
        for (int i = 0; i < 100; ++i) {
            new Thread(t, "Thread" + i).start();
        }
    }

    /**
     * 使用synchronized保证原子性与可见性
     * 早期属于重量级锁
     */
    static class T2 implements Runnable {
        private int count = 100;

        @Override
        public synchronized void run() {
            count--;
            System.out.println(Thread.currentThread().getName() + " count = " + count);
        }
    }
    static void testSynchronized() {
        T2 t2 = new T2();
        for (int i = 0; i < 100; ++i) {
            new Thread(t2, "Thread" + i).start();
        }
    }

    public static void main(String[] args) {
//        testVolatile();
        testSynchronized();
    }
}
