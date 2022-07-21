package com.github.zhuaidadaya.rikaishinikui.handler.universal.collection.sequence;

import java.util.*;

public class FastBilateral<T> extends Bilateral<T> {
    public FastBilateral(Map<T, T> relation, int size) {
        super(relation, size);
    }

    public boolean success(T in, int least) {
        return least > last && success(in);
    }

    public boolean failure(T in, int least) {
        return least < last || failure(in);
    }
}
