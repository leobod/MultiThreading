package C03_AtmoicAndOther;

import java.util.concurrent.Exchanger;

/**
 * exchanger只能2个线程之间数据交换
 */

public class T11_Exchanger {
    static Exchanger<String> exchanger = new Exchanger<>();



    public static void main(String[] args) {
        new Thread(() -> {
            String s = "T1";
            try {
                s = exchanger.exchange(s);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " : " + s);
        }, "T1").start();

        new Thread(() -> {
            String s = "T2";
            try {
                s = exchanger.exchange(s);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " : " + s);
        }, "T2").start();
    }
}
