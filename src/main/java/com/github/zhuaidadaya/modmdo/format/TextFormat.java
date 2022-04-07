package com.github.zhuaidadaya.modmdo.format;

import com.github.zhuaidadaya.modmdo.lang.Language;
import com.github.zhuaidadaya.modmdo.reads.FileReads;
import com.github.zhuaidadaya.modmdo.resourceLoader.Resources;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public abstract class TextFormat<T> {
    protected final Map<Language, JSONObject> format = new HashMap<>();

    public TextFormat(LanguageResource languageResource) {
        set(languageResource);
    }

    public void set(LanguageResource languageResource) {
        for (Language lang : languageResource.getNames()) {
            String resource = languageResource.get(lang);

            try {
                JSONObject json = new JSONObject(FileReads.read(new BufferedReader(new InputStreamReader(Resources.getResource(resource, getClass()), StandardCharsets.UTF_8))));
                format.put(lang, json);
            } catch (Exception e) {

            }
        }
    }

    public abstract T format(String key, Object... args);
}