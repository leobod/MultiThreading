package C02_VolatileAndCAS;

/**
 * 使用Double Check Lock 来构建单例模式
 * volatile与synchronized的结合使用
 *
 * 有写代码会减少其中一个if
 * 对于第一个if是为了通过null过滤掉以后来的判定，从而不需要在加锁
 * 对于第二个if是为了保证null时才创建一个单例子
 */

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
