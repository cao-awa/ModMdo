package t;

public class Test {
    public static void main(String[] args) {
        try {
            System.out.println(Class.forName("com.github.zhuaidadaya.modmdo.event.trigger.message.SendMessageTrigger").getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}