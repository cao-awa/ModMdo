package com.github.cao.awa.modmdo.format.minecraft;

import com.github.cao.awa.modmdo.format.*;
import com.github.cao.awa.modmdo.lang.*;
import com.github.cao.awa.modmdo.resourceLoader.*;
import com.github.cao.awa.modmdo.utils.usr.*;
import net.minecraft.text.*;

public class MinecraftTextFormat extends TextFormat<LiteralText> {
    public MinecraftTextFormat(Resource<Language> resource) {
        super(resource);
    }

    public LiteralText format(String key, Object... args) {
        return new LiteralText(formatted(key, args));
    }

    public LiteralText format(User user, String key, Object... args) {
        return new LiteralText(formatted(user, key, args));
    }

    public LiteralText format(Language language, String key, Object... args) {
        return new LiteralText(formatted(language, key, args));
    }
}