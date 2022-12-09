package com.github.cao.awa.modmdo.resourceLoader;

import com.github.cao.awa.modmdo.utils.io.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import it.unimi.dsi.fastutil.objects.*;

import java.io.*;
import java.nio.charset.*;
import java.util.*;

public class Resource<T> {
    private final Object2ObjectLinkedOpenHashMap<T, ObjectOpenHashSet<String>> map = new Object2ObjectLinkedOpenHashMap<>();

    public void set(T name, String resource) {
        EntrustEnvironment.nulls(
                map.get(name),
                nulls -> map.put(
                        name,
                        new ObjectOpenHashSet<>()
                )
        );
        map.get(name)
           .add(resource);
    }

    public Collection<T> getNames() {
        return map.keySet();
    }

    public ObjectOpenHashSet<String> read(T target) {
        ObjectOpenHashSet<String> result = new ObjectOpenHashSet<>();
        EntrustEnvironment.tryFor(
                get(target),
                str -> {
                    if (new File(str).isFile()) {
                        result.add(IOUtil.read(new BufferedReader(new FileReader(
                                str,
                                StandardCharsets.UTF_8
                        ))));
                    } else {
                        result.add(IOUtil.read(new BufferedReader(new InputStreamReader(
                                Resources.getResource(
                                        str,
                                        getClass()
                                ),
                                StandardCharsets.UTF_8
                        ))));
                    }
                }
        );
        return result;
    }

    public ObjectOpenHashSet<String> get(T name) {
        return map.get(name);
    }
}