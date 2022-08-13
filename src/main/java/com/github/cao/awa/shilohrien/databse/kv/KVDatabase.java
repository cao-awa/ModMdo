package com.github.cao.awa.shilohrien.databse.kv;

import com.github.cao.awa.shilohrien.databse.*;
import it.unimi.dsi.fastutil.objects.*;

public class KVDatabase<T> extends MemoryDatabase {
    private final Object2ObjectOpenHashMap<String, DataBody> map = new Object2ObjectOpenHashMap<>();

    public KVDatabase(String name, String path) {
        super(name, path);
    }

    public void insert(String key, DataBody value) {
        map.put(key, value);
    }

    public DataBody query(String key) {
        return map.get(key);
    }

    public boolean save() {
        return save("KV");
    }
}
