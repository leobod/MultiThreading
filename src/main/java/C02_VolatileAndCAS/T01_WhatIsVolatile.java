package C02_VolatileAndCAS;

public class T01_WhatIsVolatile {
    private volatile boolean running = true;

    public void m() {
        System.out.println("m start");
        while (running) {
            System.out.println("m running");
        }
        System.out.println("m end");
    }


    public static void main(String[] args) {
        T01_WhatIsVolatile t = new T01_WhatIsVolatile();

        new Thread(t::m, "t1").start();

        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Main start");
        t.running = false;
        System.out.println("Main end");
    }
}
