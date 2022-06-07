package com.github.cao.awa.modmdo.format.minecraft;

import com.github.cao.awa.modmdo.format.*;
import com.github.cao.awa.modmdo.lang.*;
import com.github.cao.awa.modmdo.resourceLoader.*;
import com.github.cao.awa.modmdo.utils.usr.*;
import net.minecraft.text.*;

public class MinecraftTextFormat extends TextFormat<LiteralTextContent> {
    public MinecraftTextFormat(Resource<String> resource) {
        super(resource);
    }

    public LiteralTextContent format(String key, Object... args) {
        return new LiteralTextContent(formatted(key, args));
    }

    public LiteralTextContent format(User user, String key, Object... args) {
        return new LiteralTextContent(formatted(user, key, args));
    }

    public LiteralTextContent format(Language language, String key, Object... args) {
        return new LiteralTextContent(formatted(language, key, args));
    }

    public LiteralTextContent format(Dictionary dictionary, String key, Object... args) {
        return new LiteralTextContent(formatted(dictionary, key, args));
    }
}