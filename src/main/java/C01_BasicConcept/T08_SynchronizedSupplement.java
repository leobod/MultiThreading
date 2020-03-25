package C01_BasicConcept;

/**
 * 对于加了synchronized的方法m1，m2，如果m1调用了m2，可以用申请到的同一把锁来叠加锁定资源
 *
 * 锁的升级过程
 * 偏向锁 -> 自旋锁 -> 重量级锁
 * 偏向锁，对于第一个访问，某把锁的线程，在Object的markword记录线程ID
 * 自旋锁，对于偏向锁出现争用时，升级为自旋锁，让后来的在旁边等待指定的次数
 * 重量级锁，当自旋锁，循环等待了很多次之后，升级而成，此时去操作系统那里申请锁
 *
 * 当线程数量少、执行时间短的时候，适合用自旋
 * 当线程多、执行时间长的时候，适合使用重量级系统锁
 */

public class T08_SynchronizedSupplement {
    static class D {
        synchronized void m1() {
            System.out.println(Thread.currentThread().getName() + " -> m1 start");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            m2();
            System.out.println(Thread.currentThread().getName() + " -> m1 end");
        }

        synchronized void m2() {
            System.out.println(Thread.currentThread().getName() + " -> m2 start");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " -> m2 end");
        }
    }

    public static void main(String[] args) {
        D d = new D();
        new Thread(() -> {
            System.out.println("T1 Running");
            d.m1();
        }).start();

        new Thread(() -> {
            System.out.println("T2 Running");
            d.m1();
        }).start();
    }
}
