package C01_BasicConcept;

import java.util.concurrent.*;

/**
 * 创建线程的方法
 * 01.使用继承Thread的方法创建线程
 * 02.通过实现Runnabel的接口的方法创建线程
 * 03.通过实现Callable的接口的方法创建线程
 * 04.使用Lambda表达式创建
 * 05.使用线程池来创建线程
 *
 * 线程中的run()方法是类方法，与其他对象的类方法一个性质，不具备特殊性
 * 而要想调用一个线程，要使用他的start()方法
 * 线程对象的start方法是由，线程调度器来执行的
 */
public class T02_HowToCreateThread {
    static class MyThread extends Thread {
        @Override
        public void run() {
            System.out.println("01.使用继承Thread的方法创建线程");
        }
    }

    static class MyThreadImp implements Runnable {
        public void run() {
            System.out.println("02.通过实现Runnabel的接口的方法创建线程");
        }
    }

    static class MyCall implements Callable<String> {
        public String call() {
            System.out.println("03.通过实现Callable的接口的方法创建线程");
            return "success";
        }
    }

    public static void main(String[] args) {
        new MyThread().start();

        new Thread(new MyThreadImp()).start();

        new Thread(() -> {
                System.out.println("04.使用Lambda表达式创建");
        }).start();

        Thread t = new Thread(new FutureTask<String>(new MyCall()));
        t.start();

        ExecutorService service = Executors.newCachedThreadPool();
        service.execute(() -> {
            System.out.println("05.使用线程池来创建线程");
        });
        service.shutdown();
    }
}
