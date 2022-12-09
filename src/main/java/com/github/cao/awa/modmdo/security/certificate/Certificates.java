package com.github.cao.awa.modmdo.security.certificate;

import com.github.cao.awa.modmdo.annotations.platform.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import it.unimi.dsi.fastutil.objects.*;

import java.util.*;

@Server
public class Certificates<T extends Certificate> {
    private final Map<String, T> certificate = new Object2ObjectArrayMap<>();
    private final Map<String, String> idToName = new Object2ObjectArrayMap<>();

    public void put(String name, T whiteList) {
        this.certificate.put(
                name,
                whiteList
        );
        EntrustEnvironment.notNull(
                whiteList,
                wl -> this.idToName.put(
                        wl.getIdentifier(),
                        wl.getName()
                )
        );
    }

    public T get(String name) {
        return this.certificate.get(name);
    }

    public T getFromId(String id) {
        return this.certificate.get(this.idToName.get(id));
    }

    public void remove(String name) {
        Certificate certificate;
        if ((certificate = this.certificate.get(name)) != null) {
            this.idToName.remove(certificate.getIdentifier());
        }
        this.certificate.remove(name);
    }

    public void removeFromId(String id) {
        this.certificate.remove(idToName.get(id));
        this.idToName.remove(id);
    }

    public Set<String> keySet() {
        return this.certificate.keySet();
    }

    public Collection<T> values() {
        return this.certificate.values();
    }

    public Set<String> identifiers() {
        return this.idToName.keySet();
    }

    public int size() {
        return this.certificate.size();
    }

    public boolean containsIdentifier(String id) {
        return this.idToName.containsKey(id);
    }

    public boolean containsName(String name) {
        return this.certificate.containsKey(name);
    }

    public void clear() {
        this.certificate.clear();
        this.idToName.clear();
    }
}
