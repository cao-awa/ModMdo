package com.github.cao.awa.modmdo.security;

import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;

import java.nio.charset.*;
import java.util.concurrent.*;

public class RandomIdentifier2 {
    private static final byte[] BYTES = EntrustParser.operation(new byte[253], chars -> {
        for (int i = 0;i < chars.length;i++) {
            if (((char)i == '\n') || ((char)i) == '\r') {
                continue;
            }
            chars[i] = (byte) i;
        }
    });
    private static final byte[] BYTES_LR = EntrustParser.operation(new byte[255], chars -> {
        for (int i = 0;i < chars.length;i++) {
            chars[i] = (byte) i;
        }
    });
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    public static String randomIdentifier() {
        return hasLrIdentifier(256);
    }

    public static String noLrIdentifier(int size) {
        byte[] bytes = new byte[size];
        for (int i = 0; i < size; i++) {
            bytes[i] = (BYTES[RANDOM.nextInt(253)]);
        }

        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static String hasLrIdentifier(int size) {
        byte[] bytes = new byte[size];
        for (int i = 0; i < size; i++) {
            bytes[i] = (BYTES_LR[RANDOM.nextInt(255)]);
        }

        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static byte[] noLrIdentifierBytes(int size) {
        byte[] bytes = new byte[size];
        for (int i = 0; i < size; i++) {
            bytes[i] = (BYTES[RANDOM.nextInt(253)]);
        }

        return bytes;
    }

    public static byte[] hasLrIdentifierBytes(int size) {
        byte[] bytes = new byte[size];
        for (int i = 0; i < size; i++) {
            bytes[i] = (BYTES_LR[RANDOM.nextInt(255)]);
        }

        return bytes;
    }
}
