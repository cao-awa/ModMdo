package com.github.cao.awa.shilohrien.databse.cache;

import it.unimi.dsi.fastutil.objects.*;

public class DataResultCache<T> {
    public Object2ObjectOpenHashMap<String, T> map = new Object2ObjectOpenHashMap<>();

    public T get(String key) {
        return map.get(key);
    }

    public boolean put(String key, T value) {
        map.put(key, value);
        return true;
    }
}
