package com.github.zhuaidadaya.modMdo.Lang;

import com.github.zhuaidadaya.modMdo.Reads.FileReads;
import com.github.zhuaidadaya.modMdo.ResourceLoader.Resources;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;

public class LanguageDictionary {
    private final LinkedHashMap<Language, JSONObject> languages = new LinkedHashMap<>();

    public LanguageDictionary(String... initFrom) {
        appendResource(initFrom);
    }

    public static void main(String[] args) {
        ;
    }

    public String getWord(Language language, String word) {
        JSONObject languageDictionary = languages.get(language).getJSONObject(Language.getNameForLanguage(language));
        return languageDictionary.get(word).toString();
    }

    public void appendResource(String[] resources) {
        for(String s : resources) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(Resources.getResource(s, getClass())));
            String resource = FileReads.read(reader);

            JSONObject languageJson = new JSONObject(resource);

            for(Object o : languageJson.keySet()) {
                JSONObject language;
                JSONObject get = languages.get(Language.getLanguageForName(o.toString()));
                language = get == null ? new JSONObject() : get;
                languages.put(Language.getLanguageForName(o.toString()), language.put(o.toString(), languageJson.get(o.toString())));
            }
        }
    }
}
