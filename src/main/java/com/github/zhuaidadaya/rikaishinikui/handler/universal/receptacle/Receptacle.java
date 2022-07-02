package com.github.zhuaidadaya.rikaishinikui.handler.universal.receptacle;

public final class Receptacle<T> {
    private T target;
    private String sub = "";

    public Receptacle(T target) {
        this.target = target;
    }

    public String getSub() {
        return sub;
    }

    public Receptacle<T> setSub(String sub) {
        this.sub = sub;
        return this;
    }

    public T get() {
        return target;
    }

    public Receptacle<T> set(T target) {
        this.target = target;
        return this;
    }
}
