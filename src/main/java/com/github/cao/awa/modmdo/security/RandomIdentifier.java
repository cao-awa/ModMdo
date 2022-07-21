package com.github.cao.awa.modmdo.security;

import com.github.cao.awa.modmdo.utils.times.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.action.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;

import java.security.*;

public class RandomIdentifier {
    private static final char[] CHARS = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.', '_', '[', ']', '/', '\\', '{', '}', '?', ',', ';', '\'', '\"', '!', '@', '#', '*', '(', ')', '&', '^', '$', '-', '=', '+', '`', '|', ' ', ':',};
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String randomIdentifier() {
        return randomIdentifier(256, false);
    }

    public static String randomIdentifier(int size, boolean noNano) {
        StringBuilder builder = new StringBuilder();
        StringBuilder nano = new StringBuilder(String.valueOf(TimeUtil.nano()));

        Do.letForUp(size, 0, noNano ? i -> {
            builder.append(CHARS[RANDOM.nextInt(CHARS.length)]);
        } : i -> {
            if (i % (size / nano.length()) == 0 && i > 1) {
                builder.append('-');
                EntrustExecution.tryTemporary(() -> {
                    builder.insert(RANDOM.nextInt(builder.length()), nano.charAt(0));
                    nano.delete(0, 1);
                });
            } else {
                builder.append(CHARS[RANDOM.nextInt(CHARS.length)]);
            }
        });


        if (! noNano) {
            builder.append(nano);
        }

        return builder.toString();
    }
}
