package com.github.cao.awa.modmdo.develop.text;

import net.minecraft.text.*;

public record Translatable(TranslatableText translatable) {
    public static Translatable translatable(String key, Object... args) {
        return new Translatable(new TranslatableText(key, args));
    }

    public MutableText text() {
        return translatable;
    }

    public String getKey() {
        return translatable.getKey();
    }

    public Object[] getArgs() {
        return translatable.getArgs();
    }
}
