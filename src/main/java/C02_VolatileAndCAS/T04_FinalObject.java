package C02_VolatileAndCAS;

/**
 * synchronized 锁定的是对象，不是代码，synchronized加在方法上等于synchronized(this)
 *
 * 下面这个小程序，t1.start() 过后通过修改 t的object
 *
 * 此时对于t2来说o是新的对象，没有锁，所以t2可以运行
 *
 * 但是一般情况下类的属性都会定义成private，所以轻易不会被修改
 *
 * 或者通过使用final来，使得对象不可修改
 *
 * 对于这个小程序中来说，如果使用final，则代码出错（o无法修改），
 * 如果把修改的那行注释，则t2永远无法运行得到。
 */

public class T04_FinalObject {
    /* final */ Object o = new Object();

    void m() {
        synchronized (o) {
            while (true) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName());
            }
        }
    }



    public static void main(String[] args) {
        T04_FinalObject t = new T04_FinalObject();

        new Thread(t::m, "t1").start();

        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Thread t2 = new Thread(t::m, "t2");

        t.o = new Object();

        t2.start();
    }
}
