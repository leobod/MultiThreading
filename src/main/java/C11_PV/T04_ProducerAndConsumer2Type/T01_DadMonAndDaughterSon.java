package C11_PV.T04_ProducerAndConsumer2Type;

import com.sun.jmx.snmp.SnmpMsg;

import java.util.concurrent.Semaphore;

public class T01_DadMonAndDaughterSon {
    Object mutex = new Object();

    Semaphore apple = new Semaphore(0);
    Semaphore orange = new Semaphore(0);

    Semaphore plate = new Semaphore(1);

    public void Dad() {
        try {
            plate.acquire();
            synchronized (mutex) {
                System.out.println("Dad Put an apple in the plate");
            }
            apple.release(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void Daughter() {
        try {
            apple.acquire();
            synchronized (mutex) {
                System.out.println("Daughter pick the apple");
            }
            plate.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void Mon() {
        try {
            plate.acquire();
            synchronized (mutex) {
                System.out.println("Mon put an orange in the plate");
            }
            orange.release(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void Son() {
        try {
            orange.acquire();
            synchronized (mutex) {
                System.out.println("Son pick an orange");
            }
            plate.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        T01_DadMonAndDaughterSon t = new T01_DadMonAndDaughterSon();

        new Thread(t::Daughter).start();
        new Thread(t::Dad).start();

        new Thread(t::Mon).start();
        new Thread(t::Son).start();
    }

}
