package C02_VolatileAndCAS;

public class T02_DCL {
    private static volatile T02_DCL INSTANCE;

    private T02_DCL() {

    }

    public static T02_DCL getInstance() {
        if (INSTANCE == null) {
            synchronized (T02_DCL.class) {
                if (INSTANCE == null) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    INSTANCE = new T02_DCL();
                }
            }
        }
        return INSTANCE;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; ++i) {
            new Thread(() -> {
                System.out.println(T02_DCL.getInstance().hashCode());
            }).start();
        }
    }
}
