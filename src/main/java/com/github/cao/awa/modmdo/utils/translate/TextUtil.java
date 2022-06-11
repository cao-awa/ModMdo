package com.github.cao.awa.modmdo.utils.translate;

import net.minecraft.text.*;

public class TextUtil {
    public static TranslatableTextContent formatRule(String head, String info) {
        return TextUtil.translatable(head + "." + info + ".rule.format");
    }

    public static TranslatableTextContent translatable(String key, Object... args) {
        return new TranslatableTextContent(key, args);
    }

    public static LiteralTextContent literal(String str) {
        return new LiteralTextContent(str);
    }

    public static MutableText literalText(String str) {
        return MutableText.of(new LiteralTextContent(str));
    }
}
