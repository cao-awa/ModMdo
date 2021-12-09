package com.github.zhuaidadaya.modMdo.configure.modmdo;

import net.minecraft.client.option.CyclingOption;
import net.minecraft.text.TranslatableText;

public class ModMdoOption {
    public static final CyclingOption<Boolean> TEST = CyclingOption.create("options.modmdo", new TranslatableText("options.modmdo.enable"), new TranslatableText("options.modmdo.disable"), gameOptions -> {
        return true;
//        return gameOptions.backgroundForChatOnly;
    }, (gameOptions, option, enable) -> {
        System.out.println(enable);
//        gameOptions.backgroundForChatOnly = backgroundForChatOnly;
    });
}
