package com.github.zhuaidadaya.rikaishinikui.handler.universal.collection.sequence;

import java.util.*;

public class Bilateral<T> {
    public final Map<T, T> relation;
    public final T[] lefts;
    public int last = -1;

    public Bilateral(Map<T, T> relation, int size) {
        this.relation = relation;
        if (size == -1) {
            this.lefts = (T[])new Object[512];
        } else {
            this.lefts = (T[]) new Object[(size >> 1) + 1];
        }
    }

    public boolean failure(T in) {
        return ! success(in);
    }

    public boolean success(T in) {
        return relation.containsKey(in) ? left(in) : right(in);
    }

    public boolean ensureSuccess(T in) {
        if (relation.containsKey(in)) {
            return left(in);
        }
        if (relation.containsValue(in)) {
            return right(in);
        }
        return false;
    }

    public boolean ensureFailure(T in) {
        if (relation.containsKey(in)) {
            if (relation.get(in).equals(now())) {
                return !right(in);
            }
            return !left(in);
        }
        if (relation.containsValue(in)) {
            return !right(in);
        }
        return false;
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

    public T now() {
        return last > -1 ? lefts[last] : null;
    }

    public T last(int count) {
        int index = last - count;
        return  index > -1 ? lefts[index] : null;
    }
}