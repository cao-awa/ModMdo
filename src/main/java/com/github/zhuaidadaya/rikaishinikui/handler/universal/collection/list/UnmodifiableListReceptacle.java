package com.github.zhuaidadaya.rikaishinikui.handler.universal.collection.list;

import java.util.*;
import java.util.function.*;

public class UnmodifiableListReceptacle<T> {
    private final List<T> list;

    public UnmodifiableListReceptacle(List<T> list) {
        this.list = list;
    }

    public T get(int index) {
        return list.get(index);
    }

    public boolean contains(T target) {
        return list.contains(target);
    }

    public boolean containsAll(Collection<T> target) {
        return list.containsAll(target);
    }

    public int size() {
        return list.size();
    }

    public void foreach(Consumer<T> action) {
        list.forEach(action);
    }
}
