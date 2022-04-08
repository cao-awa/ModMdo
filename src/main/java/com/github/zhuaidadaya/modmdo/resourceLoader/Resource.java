package com.github.zhuaidadaya.modmdo.resourceLoader;

import com.github.zhuaidadaya.modmdo.lang.Language;
import com.github.zhuaidadaya.modmdo.reads.FileReads;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Resource<T> {
    private final Map<T, String> map = new HashMap<>();

    public void set(T name, String resource) {
        map.put(name,resource);
    }

    public String get(T name) {
        return map.get(name);
    }

    public Collection<T> getNames() {
        return map.keySet();
    }

    public String read(T target) {
        return FileReads.read(new BufferedReader(new InputStreamReader(Resources.getResource(get(target), getClass()), StandardCharsets.UTF_8)));
    }
}