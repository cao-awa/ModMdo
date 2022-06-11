package com.github.cao.awa.modmdo.utils.translate;

import net.minecraft.text.TranslatableTextContent;

public class TranslateUtil {
    public static TranslatableTextContent formatRule(String head, String info) {
        return new TranslatableTextContent(head + "." + info + ".rule.format");
    }

    public static TranslatableTextContent translatableText(String key, Object... args) {
        return new TranslatableTextContent(key,args);
    }
}
