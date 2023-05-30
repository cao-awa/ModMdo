package com.github.cao.awa.modmdo.security.certificate;

import com.github.cao.awa.modmdo.annotations.platform.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import it.unimi.dsi.fastutil.objects.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.*;

@Server
public class Certificates<T extends Certificate> {
    private final Map<String, T> certificates = new Object2ObjectArrayMap<>();
    private final Map<String, String> idToName = new Object2ObjectArrayMap<>();

    public void put(@NotNull String name, @NotNull T whiteList) {
        this.certificates.put(
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

    public T getFromId(@NotNull String id) {
        return this.certificates.get(this.idToName.get(id));
    }

    public void remove(@NotNull String name) {
        Certificate certificate;
        if ((certificate = this.certificates.get(name)) != null) {
            this.idToName.remove(certificate.getIdentifier());
        }
        this.certificates.remove(name);
    }

    public void removeFromId(@NotNull String id) {
        this.certificates.remove(idToName.get(id));
        this.idToName.remove(id);
    }

    public Set<String> keySet() {
        return this.certificates.keySet();
    }

    public Collection<T> values() {
        return this.certificates.values();
    }

    public Set<String> identifiers() {
        return this.idToName.keySet();
    }

    public int size() {
        return this.certificates.size();
    }

    public boolean containsIdentifier(String id) {
        return this.idToName.containsKey(id);
    }

    public boolean containsName(@NotNull String name) {
        return this.certificates.containsKey(name);
    }

    public void clear() {
        this.certificates.clear();
        this.idToName.clear();
    }

    public T get(@NotNull String name) {
        return this.certificates.get(name);
    }

    public void forEach(@NotNull Consumer<T> action) {
        this.certificates.values().forEach(action);
    }
}
