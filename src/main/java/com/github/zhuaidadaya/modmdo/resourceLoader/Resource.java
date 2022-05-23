package com.github.zhuaidadaya.modmdo.resourceLoader;

import com.github.zhuaidadaya.modmdo.lang.Language;
import com.github.zhuaidadaya.modmdo.reads.FileReads;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import it.unimi.dsi.fastutil.objects.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Resource<T> {
    private final Object2ObjectLinkedOpenHashMap<T, ObjectOpenHashSet<String>> map = new Object2ObjectLinkedOpenHashMap<>();

    public void set(T name, String resource) {
        EntrustExecution.executeNull(map.get(name), m -> {}, nu -> map.put(name, new ObjectOpenHashSet<>()));
        map.get(name).add(resource);
    }

    public ObjectOpenHashSet<String> get(T name) {
        return map.get(name);
    }

    public Collection<T> getNames() {
        return map.keySet();
    }

    public ObjectOpenHashSet<String> read(T target) {
        ObjectOpenHashSet<String > result = new ObjectOpenHashSet<>();
        for (String s : get(target)) {
            if (s.startsWith("/")) {
                result.add(FileReads.read(new BufferedReader(new InputStreamReader(Resources.getResource(s, getClass()), StandardCharsets.UTF_8))));
            } else {
                EntrustExecution.tryTemporary(() -> {
                    result.add(FileReads.read(new BufferedReader(new FileReader(s, StandardCharsets.UTF_8))));
                });
            }
        }
        return result;
    }
}