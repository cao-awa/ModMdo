package com.github.cao.awa.modmdo.utils.text;

import com.github.cao.awa.modmdo.develop.text.*;
import net.minecraft.text.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.minecraftTextFormat;

public class TextUtil {
    public static Translatable formatRule(String head, String info) {
        return TextUtil.translatable(head + "." + info + ".rule.format");
    }

    public static Translatable translatable(String key, Object... args) {
        return Translatable.translatable(key, args);
    }

    public static Translatable translatable(Text text) {
        if (text instanceof TranslatableText translatable)
            return new Translatable(translatable);
        return Translatable.translatable(text.getString());
    }

    public static Literal literal(String str) {
        return new Literal(new LiteralText(str));
    }

    public static Literal format(String key, Object... args) {
        return minecraftTextFormat.format(key, args);
    }
}
