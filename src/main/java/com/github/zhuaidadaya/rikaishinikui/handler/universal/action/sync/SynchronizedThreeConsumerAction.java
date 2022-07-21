package com.github.zhuaidadaya.rikaishinikui.handler.universal.action.sync;

import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.*;

import java.util.function.*;

public class SynchronizedThreeConsumerAction<X, Y,Z> {
    private final ThreeConsumer<X, Y, Z> action;
    private final Object mutex;

    public SynchronizedThreeConsumerAction(ThreeConsumer<X, Y, Z> action) {
        this.mutex = this;
        this.action = action;
    }

    public void action(X x, Y y,Z z) {
        synchronized (mutex) {
            action.accept(x, y,z);
        }
    }
}
