package com.github.zhuaidadaya.modmdo.format.minecraft;

import com.github.zhuaidadaya.modmdo.lang.Language;
import com.github.zhuaidadaya.modmdo.resourceLoader.Resource;
import com.github.zhuaidadaya.modmdo.format.TextFormat;
import com.github.zhuaidadaya.modmdo.utils.usr.*;
import net.minecraft.text.LiteralText;

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