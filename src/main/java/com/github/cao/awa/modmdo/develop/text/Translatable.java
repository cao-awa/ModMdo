package com.github.cao.awa.modmdo.develop.text;

import net.minecraft.text.*;

public record Translatable(TranslatableText text) {
    public static Translatable translatable(String key, Object... args) {
        return new Translatable(new TranslatableText(key, args));
    }

    public String getKey() {
        return text.getKey();
    }

    public Object[] getArgs() {
        return text.getArgs();
    }
}
