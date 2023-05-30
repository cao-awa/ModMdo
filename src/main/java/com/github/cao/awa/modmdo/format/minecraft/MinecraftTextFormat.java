package com.github.cao.awa.modmdo.format.minecraft;

import com.github.cao.awa.modmdo.develop.text.*;
import com.github.cao.awa.modmdo.format.*;
import com.github.cao.awa.modmdo.lang.*;
import com.github.cao.awa.modmdo.usr.*;
import com.github.cao.awa.modmdo.utils.text.*;

public class MinecraftTextFormat extends TextFormat<Literal> {
    public MinecraftTextFormat() {
        super();
    }

    public Literal format(String key, Object... args) {
        return TextUtil.literal(formatted(key, args));
    }

    public Literal format(User user, String key, Object... args) {
        return TextUtil.literal(formatted(user, key, args));
    }

    public Literal format(Language language, String key, Object... args) {
        return TextUtil.literal(formatted(language, key, args));
    }

    public Literal format(Dictionary dictionary, String key, Object... args) {
        return TextUtil.literal(formatted(dictionary, key, args));
    }

    public Literal format(Dictionary dictionary, Translatable translatable) {
        return TextUtil.literal(formatted(dictionary, translatable.getKey(), translatable.getArgs()));
    }
}