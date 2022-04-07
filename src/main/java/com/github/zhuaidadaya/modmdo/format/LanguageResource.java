package com.github.zhuaidadaya.modmdo.format;

import com.github.zhuaidadaya.modmdo.lang.Language;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class LanguageResource {
    private final Map<Language, String> map = new HashMap<>();

    public void set(Language name, String resource) {
        map.put(name,resource);
    }

    public String get(Language name) {
        return map.get(name);
    }

    public Collection<Language> getNames() {
        return map.keySet();
    }
}
