package com.github.zhuaidadaya.rikaishinikui.handler.universal.collection.sequence;

import java.util.*;

public class Bilateral<T> {
    public final Map<T, T> relation;
    public final T[] lefts;
    public int last = -1;

    public Bilateral(Map<T, T> relation, int size) {
        this.relation = relation;
        this.lefts = (T[]) new Object[(size >> 1) + 1];
    }

    public boolean failure(T in) {
        return ! success(in);
    }

    public boolean success(T in) {
        return relation.containsKey(in) ? left(in) : right(in);
    }

    private boolean left(T left) {
        this.lefts[++last] = left;
        return true;
    }

    private boolean right(T right) {
        return last != - 1 && relation.get(lefts[last--]).equals(right);
    }

    public boolean isDone() {
        return last == -1;
    }
}

