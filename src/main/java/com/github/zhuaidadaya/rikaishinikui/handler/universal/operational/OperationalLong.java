package com.github.zhuaidadaya.rikaishinikui.handler.universal.operational;

import java.util.function.*;

public class OperationalLong extends Operational<Long> {
    private Long longValue;
    private Consumer<Long> callback;

    public OperationalLong(Long base) {
        longValue = base;
    }

    public OperationalLong(Integer base) {
        longValue = Long.valueOf(base);
    }

    public OperationalLong() {
        longValue = 0L;
    }

    public Long get() {
        return longValue;
    }

    public Long set(Long value) {
        longValue = value;
        callback(longValue);
        return value;
    }

    public OperationalLong callback(Consumer<Long> callback) {
        this.callback = callback;
        return this;
    }

    private void callback(Long LongValue) {
        callback.accept(LongValue);
    }

    public Long add() {
        callback(longValue++);
        return longValue;
    }

    public Long add(Long value, Long amplifier) {
        return add(value * amplifier);
    }

    public Long add(Long value) {
        longValue += value;
        callback(longValue);
        return longValue;
    }

    public Long reduce(Long value, Long amplifier) {
        return reduce(value * amplifier);
    }

    public Long reduce(Long value) {
        longValue -= value;
        callback(longValue);
        return longValue;
    }

    public Long add(Integer value, Long amplifier) {
        return add(value * amplifier);
    }

    public Long add(Integer value) {
        longValue += value;
        callback(longValue);
        return longValue;
    }

    public Long reduce(Integer value, Long amplifier) {
        return reduce(value * amplifier);
    }

    public Long reduce(Integer value) {
        longValue -= value;
        callback(longValue);
        return longValue;
    }

    public Long reduce() {
        callback(longValue--);
        return longValue;
    }

    public Long multiply(Long value) {
        callback(longValue *= value);
        return longValue;
    }

    public Long divide(Long value) {
        callback(longValue /= value);
        return longValue;
    }
}
