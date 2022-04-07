package com.github.zhuaidadaya.modmdo.utils.translate;

import net.minecraft.text.TranslatableText;

public class TranslateUtil {
    public static TranslatableText formatRule(String head, String info) {
        return new TranslatableText(head + "." + info + ".rule.format");
    }
}
