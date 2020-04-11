package C03_AtmoicAndOther;

import java.util.Random;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/**
 * Phaser结合了CountDownLatch与CyclicBarrier
 * 支持在指定步骤，指定的数量做什么事情。
 *
 * bulkRegister(7)      方法bulkRegister()可以批量增加parties数量。
 *
 * 方法arriveAndAwaitAdvance() 让对应的对象阶段性等待,当计数不足时，线程呈阻塞状态，不继续向下运行。
 * 方法arriveAndDeregister()的作用是使当前线程退出，并且使parties值减1
 * 方法register()的作用是使当前线程加入，并且使parties值加1
 *
 */

public class T08_Phaser {
    static Random r = new Random();
    static CustomPhaser phaser = new CustomPhaser();

    static class CustomPhaser extends Phaser {
        @Override
        protected boolean onAdvance(int phase, int registeredParties) {
            switch (phase) {
                case 0:
                    System.out.println("Step 1 : " + registeredParties);
                    System.out.println();
                    return false;
                case 1:
                    System.out.println("Step 2 : " + registeredParties);
                    System.out.println();
                    return false;
                case 2:
                    System.out.println("Step 3 : " + registeredParties);
                    System.out.println();
                    return false;
                case 3:
                    System.out.println("Step end : " + registeredParties);
                    return true;
                default:
                    return true;
            }
        }
    }

    static void customSleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class Person implements Runnable {
        String name;

        public Person(String name) {
            this.name = name;
        }

        public void arrive() {
            customSleep(100);
            System.out.println(name + " 到来现场! ");
            phaser.arriveAndAwaitAdvance();
        }

        public void eat() {
            customSleep(100);
            System.out.println(name + " 正在吃饭! ");
            phaser.arriveAndAwaitAdvance();
        }

        public void leave() {
            customSleep(100);
            System.out.println(name + " 准备离开! ");
            phaser.arriveAndAwaitAdvance();
        }

        public void hug() {
            if (name.equals("A") || name.equals("B")) {
                customSleep(r.nextInt(100));
                System.out.println(name + " ... ");
                phaser.arriveAndAwaitAdvance();
            } else {
                phaser.arriveAndDeregister();
//                phaser.register();
            }
        }

        @Override
        public void run() {
            arrive();
            eat();
            leave();
            hug();
        }
    }


    public static void main(String[] args) {
        phaser.bulkRegister(7);

        for (int i = 0; i < 5; ++i) {
            new Thread(new Person("P" + i)).start();
        }


        new Thread(new Person("A")).start();
        new Thread(new Person("B")).start();
    }

}
