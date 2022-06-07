package com.github.cao.awa.modmdo.whitelist;

import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import it.unimi.dsi.fastutil.objects.*;

public class Certificates<T extends Certificate> {
    private final Object2ObjectArrayMap<String, T> certificate = new Object2ObjectArrayMap<>();
    private final Object2ObjectArrayMap<String, String> idToName = new Object2ObjectArrayMap<>();

    public void put(String name,T whiteList) {
        certificate.put(name, whiteList);
        EntrustExecution.notNull(whiteList, wl -> idToName.put(wl.getIdentifier(), wl.getName()));
    }

    public T get(String name) {
        return certificate.get(name);
    }

    public T getFromId(String id) {
        return certificate.get(idToName.get(id));
    }

    public void remove(String name) {
        idToName.remove(certificate.get(name).getIdentifier());
        certificate.remove(name);
    }

    public void removeFromId(String id) {
        certificate.remove(idToName.get(id));
        idToName.remove(id);
    }

    public ObjectSet<String> keySet() {
        return certificate.keySet();
    }

    public ObjectCollection<T> values() {
        return certificate.values();
    }

    public ObjectSet<String> identifiers() {
        return idToName.keySet();
    }

    public int size() {
        return certificate.size();
    }

    public boolean containsIdentifier(String id) {
        return idToName.containsKey(id);
    }

    public boolean containsName(String name) {
        return certificate.containsKey(name);
    }

    public void clear() {
        certificate.clear();
        idToName.clear();
    }
}
