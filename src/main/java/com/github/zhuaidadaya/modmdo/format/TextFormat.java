package com.github.zhuaidadaya.modmdo.format;

import com.github.zhuaidadaya.modmdo.lang.Language;
import com.github.zhuaidadaya.modmdo.resourceLoader.Resource;
import com.github.zhuaidadaya.modmdo.utils.usr.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import org.json.JSONObject;

import java.util.Map;

import static com.github.zhuaidadaya.modmdo.storage.Variables.getLanguage;

public abstract class TextFormat<T> {
    protected final Map<Language, Map<String, String>> format = new Object2ObjectLinkedOpenHashMap<>();

    public TextFormat(Resource<Language> resource) {
        set(resource);
    }

    public void set(Resource<Language> resource) {
        for (Language lang : resource.getNames()) {
            try {
                Map<String, String> map = new Object2ObjectLinkedOpenHashMap<>();
                JSONObject json = new JSONObject(resource.read(lang));
                for (String s : json.keySet()) {
                    map.put(s, json.getString(s));
                }
                format.put(lang, map);
            } catch (Exception e) {

            }
        }
    }

    public abstract T format(String key, Object... args);

    public String formatted(String key, Object... args) {
        try {
            String formatReturn = format.get(getLanguage()).get(key);

            for (Object o : args) {
                try {
                    formatReturn = formatReturn.replaceFirst("%s", o.toString());
                } catch (Exception ex) {
                    return formatReturn;
                }
            }
            return formatReturn;
        } catch (Exception e) {
            return "";
        }
    }

    public String formatted(User user, String key, Object... args) {
        try {
            String formatReturn = format.get(user.getLanguage()).get(key);

            for (Object o : args) {
                try {
                    formatReturn = formatReturn.replaceFirst("%s", o.toString());
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