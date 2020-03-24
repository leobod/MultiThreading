package C01_BasicConcept;

/**
 * 使用synchronized来锁定资源对象
 */

public class T05_SynchronizedObject {
    static class T {
        private int count = 10;
        private Object o = new Object();

        public void m() {
            synchronized (o) {
                count--;
                System.out.println(Thread.currentThread().getName() + "count = " + count);
            }
        }
    }

    static class TT01 extends Thread {
        private T t;

        public TT01(T t) {
            this.t = t;
        }

        @Override
        public void run() {
            System.out.println("TT01");
            for (int i = 0; i < 3; i++) {
                t.m();
            }
        }
    }
    static class TT02 extends Thread {
        private T t;

        public TT02(T t) {
            this.t = t;
        }

        @Override
        public void run() {
            System.out.println("TT02");
            for (int i = 0; i < 3; i++) {
                t.m();
            }
        }
    }

    public static void main(String[] args) {
        T t = new T();
        new TT01(t).start();
        new TT02(t).start();
    }
}
