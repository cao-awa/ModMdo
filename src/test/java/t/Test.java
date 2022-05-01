package t;

import com.github.zhuaidadaya.modmdo.utils.times.TimeUtil;
import it.unimi.dsi.fastutil.Hash;
import org.json.*;

import java.util.*;

public class Test {
    public static void main(String[] args) {
        long nano = TimeUtil.nano();

        byte[] b = new byte[1024];
        Random r = new Random();

        int target = 100000;

        for (int i = 0;i < target;i++) {
            r.nextBytes(b);
        }

        System.out.println(((TimeUtil.nano() - nano) / 1000000d) + "ms");
    }
}
