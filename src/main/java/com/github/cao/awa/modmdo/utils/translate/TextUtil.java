package com.github.cao.awa.modmdo.utils.translate;

import net.minecraft.text.*;

public class TextUtil {
    public static TranslatableText formatRule(String head, String info) {
        return TextUtil.translatable(head + "." + info + ".rule.format");
    }

    public static TranslatableText translatable(String key, Object... args) {
        return new TranslatableText(key,args);
    }

    public static LiteralText literal(String str) {
        return new LiteralText(str);
    }
}
