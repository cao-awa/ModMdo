package t;

import org.json.JSONObject;

import java.util.Collection;
import java.util.function.Consumer;

public class ConsumerTest<T> {
    private JSONObject t;

    public void test(Consumer<? super JSONObject> action) {
        action.accept(t);
    }

    public static void main(String[] args) {
        new ConsumerTest<>().test(e -> {
            System.out.println("consumer");
        });
    }
}
