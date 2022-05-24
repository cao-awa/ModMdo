package com.github.cao.awa.modmdo.utils.translate;

import net.minecraft.text.TranslatableText;

public class TranslateUtil {
    public static TranslatableText formatRule(String head, String info) {
        return new TranslatableText(head + "." + info + ".rule.format");
    }

    public static TranslatableText translatableText(String key, Object... args) {
        return new TranslatableText(key,args);
    }
}
