package com.github.zhuaidadaya.modmdo.format.console;

import com.github.zhuaidadaya.modmdo.format.LanguageResource;
import com.github.zhuaidadaya.modmdo.format.TextFormat;

import static com.github.zhuaidadaya.modmdo.storage.Variables.language;

public class ConsoleTextFormat extends TextFormat<String> {
    public ConsoleTextFormat(LanguageResource languageResource) {
        super(languageResource);
    }

    public String format(String source, Object... args) {
        try {
            String formatReturn = format.get(language).getString(source);

            for(Object o : args) {
                try {
                    formatReturn = formatReturn.replaceFirst("%s" ,o.toString());
                } catch (Exception ex) {
                    return formatReturn;
                }
            }
            return formatReturn;
        } catch (Exception e) {
            return "";
        }
    }
}