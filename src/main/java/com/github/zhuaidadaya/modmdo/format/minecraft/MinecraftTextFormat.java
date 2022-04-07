package com.github.zhuaidadaya.modmdo.format.minecraft;

import com.github.zhuaidadaya.modmdo.format.LanguageResource;
import com.github.zhuaidadaya.modmdo.format.TextFormat;
import net.minecraft.text.LiteralText;

import static com.github.zhuaidadaya.modmdo.storage.Variables.language;

public class MinecraftTextFormat extends TextFormat<LiteralText> {
    public MinecraftTextFormat(LanguageResource languageResource) {
        super(languageResource);
    }

    public LiteralText format(String source, Object... args) {
        try {
            String formatReturn = format.get(language).getString(source);

            for(Object o : args) {
                try {
                    formatReturn = formatReturn.replaceFirst("%s" ,o.toString());
                } catch (Exception ex) {
                    return new LiteralText(formatReturn);
                }
            }
            return new LiteralText(formatReturn);
        } catch (Exception e) {
            return new LiteralText("");
        }
    }
}