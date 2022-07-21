package com.github.zhuaidadaya.rikaishinikui.handler.universal.action.sync;

import java.util.function.*;

public class SynchronizedBiConsumerAction<T, Y> {
    private final BiConsumer<T, Y> action;
    private final Object mutex;

    public SynchronizedBiConsumerAction(BiConsumer<T, Y> action) {
        this.mutex = this;
        this.action = action;
    }

    public void action(T t, Y y) {
        synchronized (mutex) {
            action.accept(t, y);
        }
    }
}
