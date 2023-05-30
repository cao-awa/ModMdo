package com.github.zhuaidadaya.rikaishinikui.handler.universal.activity;

import java.util.function.*;

public class ActivityObject<T> {
    private final T obj;
    private boolean active;

    public ActivityObject(T obj) {
        this.obj = obj;
        active = true;
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
        this.active = active;
        return this;
    }

    public ActivityObject<T> active() {
        this.active = true;
        return this;
    }

    public ActivityObject<T> invalid() {
        this.active = false;
        return this;
    }
}
