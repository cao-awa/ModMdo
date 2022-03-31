package com.github.zhuaidadaya.modmdo.identifier;

import java.util.Random;

public class RandomIdentifier {
    private static final String[] bytes = {
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
            "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            ".", "_"
    };
    private static final Random random = new Random();

    public static String randomIdentifier() {
        return randomIdentifier(128);
    }

    public static String randomIdentifier(int size) {
        StringBuilder builder = new StringBuilder();
        int i = 0;
        StringBuilder nano = new StringBuilder(String.valueOf(System.nanoTime()));
        for (; i < size; i++) {
            if (i % 12 == 0 & i > 1) {
                builder.append("-");
                if (builder.length() > 0) {
                    builder.insert(random.nextInt(builder.length()), nano.charAt(0));
                    nano.delete(0, 1);
                }
            } else {
                builder.append(randomString());
            }
        }

        builder.append(nano);

        return builder.toString();
    }

    private static String randomString() {
        return bytes[random.nextInt(bytes.length)];
    }
}
