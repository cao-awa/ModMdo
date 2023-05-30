package com.github.cao.awa.modmdo.security.level;

import com.github.cao.awa.modmdo.annotations.platform.*;

@Client
public enum SecureLevel {
    UNEQUAL_KEY, UNEQUAL_ID, NO_SECURE;

    public static SecureLevel of(String target) {
        return switch (target.toLowerCase()) {
            case "unequal_key" -> UNEQUAL_KEY;
            case "unequal_id" -> UNEQUAL_ID;
            case "no_secure" -> NO_SECURE;
            default -> throw new IllegalArgumentException("Unknown the secure type: " + target);
        };
    }
}
