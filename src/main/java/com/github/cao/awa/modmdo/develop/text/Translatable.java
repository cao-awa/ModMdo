package com.github.cao.awa.modmdo.develop.text;

import net.minecraft.text.*;

public final class Translatable extends TranslatableText{
    public Translatable(String key, Object... args) {
        super(key, args);
    }

    public Translatable(TranslatableText text) {
        super(text.getKey(), text.getArgs());
    }

    public static Translatable translatable(String key, Object... args) {
        return new Translatable(key, args);
    }

    public String getKey() {
        return super.getKey();
    }

    public Object[] getArgs() {
        return super.getArgs();
    }
}
