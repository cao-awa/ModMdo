package com.github.zhuaidadaya.rikaishinikui.handler.universal.activity;

import java.util.function.*;

public class ActivityObject<T> {
    private final T obj;
    private boolean active;
    private boolean needEnsure = false;
    private String ensureKey;

    public ActivityObject(T obj) {
        this.obj = obj;
        active = true;
    }

    public ActivityObject(T obj, String ensureKey) {
        this.obj = obj;
        active = true;
        this.needEnsure = true;
        this.ensureKey = ensureKey;
    }

    public T get() {
        return obj;
    }

    public T action(Consumer<T> action) {
        if (isActive()) {
            action.accept(this.obj);
        }
        return obj;
    }

    public boolean isActive() {
        return active;
    }

    public ActivityObject<T> setActive(boolean active) {
        if (!needEnsure) {
            this.active = active;
        }
        return this;
    }

    public ActivityObject<T> active() {
        if (!needEnsure) {
            this.active = true;
        }
        return this;
    }

    public ActivityObject<T> invalid() {
        if (!needEnsure) {
            this.active = false;
        }
        return this;
    }

    public ActivityObject<T> setActive(boolean active, String ensureKey) {
        if (ensureKey.equals(this.ensureKey)) {
            this.active = active;
        }
        return this;
    }

    public ActivityObject<T> active(String ensureKey) {
        if (ensureKey.equals(this.ensureKey)) {
            this.active = true;
        }
        return this;
    }

    public ActivityObject<T> invalid(String ensureKey) {
        if (ensureKey.equals(this.ensureKey)) {
            this.active = false;
        }
        return this;
    }
}
