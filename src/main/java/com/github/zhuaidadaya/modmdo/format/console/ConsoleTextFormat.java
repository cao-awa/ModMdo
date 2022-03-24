package com.github.zhuaidadaya.modmdo.format.console;

import com.github.zhuaidadaya.modmdo.lang.Language;
import com.github.zhuaidadaya.modmdo.reads.FileReads;
import com.github.zhuaidadaya.modmdo.resourceLoader.Resources;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

import static com.github.zhuaidadaya.modmdo.storage.Variables.language;

public class ConsoleTextFormat {
    private final Map<Language, JSONObject> format = new Object2ObjectRBTreeMap<>();

    public ConsoleTextFormat(LanguageResource languageResource) {
        for(Language lang : languageResource.getNames()) {
            String resource = languageResource.get(lang);

            try {
                JSONObject json = new JSONObject(FileReads.read(new BufferedReader(new InputStreamReader(Resources.getResource(resource, getClass())))));
                format.put(lang,json);
            } catch (Exception e) {

            }
        }
    }

    public String format(String source,Object... args) {
        try {
            String formatReturn = format.get(language).getString(source);

            for(Object o : args) {
                try {
                    formatReturn = formatReturn.formatted(o.toString());
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
