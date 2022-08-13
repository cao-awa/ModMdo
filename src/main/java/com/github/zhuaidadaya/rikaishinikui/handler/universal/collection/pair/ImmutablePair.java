package com.github.zhuaidadaya.rikaishinikui.handler.universal.collection.pair;

public class ImmutablePair<L, R> {
    private final L left;
    private final R right;

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }

    public ImmutablePair() {
        this.left = null;
        this.right = null;
    }

    public ImmutablePair(L left, R right) {
        this.left = left;
        this.right = right;
    }
}
