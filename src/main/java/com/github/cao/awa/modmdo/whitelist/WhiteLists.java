package com.github.cao.awa.modmdo.whitelist;

import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import it.unimi.dsi.fastutil.objects.*;

public class WhiteLists<T extends Whitelist> {
    private final Object2ObjectArrayMap<String, T> whitelist = new Object2ObjectArrayMap<>();
    private final Object2ObjectArrayMap<String, String> idToName = new Object2ObjectArrayMap<>();

    public void put(String name,T whiteList) {
        whitelist.put(name, whiteList);
        EntrustExecution.notNull(whiteList, wl -> idToName.put(wl.getIdentifier(), wl.getName()));
    }

    public T get(String name) {
        return whitelist.get(name);
    }

    public T getFromId(String id) {
        return whitelist.get(idToName.get(id));
    }

    public void remove(String name) {
        idToName.remove(whitelist.get(name).getIdentifier());
        whitelist.remove(name);
    }

    public void removeFromId(String id) {
        whitelist.remove(idToName.get(id));
        idToName.remove(id);
    }

    public ObjectSet<String> keySet() {
        return whitelist.keySet();
    }

    public ObjectCollection<T> values() {
        return whitelist.values();
    }

    public ObjectSet<String> identifiers() {
        return idToName.keySet();
    }

    public int size() {
        return whitelist.size();
    }

    public boolean containsIdentifier(String id) {
        return idToName.containsKey(id);
    }

    public boolean containsName(String name) {
        return whitelist.containsKey(name);
    }

    public void clear() {
        whitelist.clear();
        idToName.clear();
    }
}
