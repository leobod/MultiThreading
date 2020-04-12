package C04_LockSupportAndQ1Q2;

import java.util.ArrayList;
import java.util.List;

/**
 * 实现一个容器，提供两个方法，add,size，写2个线程
 * 线程1， 添加10个元素
 * 线程2， 实时监控个数，个数到5时，线程2给出提示并结束
 *
 */

/**
 * 没有加锁，也没有使用volatile，使得对象可见，所以t.size(),并不能读取到size
 */


public class T03_Q1 {
    List lists = new ArrayList();

    public void add(Object o) {
        lists.add(0);
    }

    public int size() {
        return lists.size();
    }


    public static void main(String[] args) {
        T03_Q1 t = new T03_Q1();

        new Thread(() -> {
            for (int i = 0; i < 10; ++i) {
                t.add(new Object());
                System.out.println("add " + i);
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "t1").start();

        new Thread(() -> {
            while (true) {
                if (t.size() == 5) {
                    break;
                }
            }
            System.out.println("t2 end");
        }, "t2").start();
    }

}
