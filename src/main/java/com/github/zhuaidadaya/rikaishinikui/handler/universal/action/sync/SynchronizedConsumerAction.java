package com.github.zhuaidadaya.rikaishinikui.handler.universal.action.sync;

import java.util.function.*;

public class SynchronizedConsumerAction<T> {
    private final Consumer<T> action;
    private final Object mutex;

    public SynchronizedConsumerAction(Consumer<T> action) {
        this.mutex = this;
        this.action = action;
    }

    public void action(T t) {
        synchronized (mutex) {
            action.accept(t);
        }
    }
}
