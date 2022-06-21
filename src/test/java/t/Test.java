package t;

import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.runnable.*;

public class Test {
    public static void main(String[] args) {
        EntrustExecution.tryTemporary(() -> {
            ReusableThread thread = new ReusableThread(() -> {
                System.out.println("awww");
                System.out.println(Thread.currentThread().getName());
            });
            thread.execute();

            Thread.sleep(100);
            thread.execute();

            Thread.sleep(100);
            thread.execute();
        }, Throwable::printStackTrace);
    }
}