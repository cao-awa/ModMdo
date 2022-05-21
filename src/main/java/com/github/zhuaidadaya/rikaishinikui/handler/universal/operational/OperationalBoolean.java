package com.github.zhuaidadaya.rikaishinikui.handler.universal.operational;

import java.util.function.*;

public class OperationalBoolean extends Operational<Boolean> {
    private Boolean BooleanValue;
    private Consumer<Boolean> callback;

    public OperationalBoolean(Boolean base) {
        BooleanValue = base;
    }

    public OperationalBoolean() {
        BooleanValue = false;
    }

    public Boolean get() {
        return BooleanValue;
    }

    public Boolean reverse() {
        callback(! BooleanValue);
        return BooleanValue;
    }

    public Boolean set(Boolean value) {
        BooleanValue = value;
        callback(BooleanValue);
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
