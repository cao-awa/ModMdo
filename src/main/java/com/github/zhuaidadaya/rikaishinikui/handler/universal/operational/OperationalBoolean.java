package com.github.zhuaidadaya.rikaishinikui.handler.universal.operational;

import java.util.function.*;

public class OperationalBoolean extends Operational<Boolean> {
    private Boolean booleanValue;
    private Consumer<Boolean> callback;

    public OperationalBoolean(Boolean base) {
        booleanValue = base;
    }

    public OperationalBoolean() {
        booleanValue = false;
    }

    public Boolean get() {
        return booleanValue;
    }

    public Boolean reverse() {
        callback(! booleanValue);
        return booleanValue;
    }

    public Boolean set(Boolean value) {
        booleanValue = value;
        callback(booleanValue);
        return value;
    }

    public OperationalBoolean callback(Consumer<Boolean> callback) {
        this.callback = callback;
        return this;
    }

    private void callback(Boolean longValue) {
        callback.accept(longValue);
    }
}
