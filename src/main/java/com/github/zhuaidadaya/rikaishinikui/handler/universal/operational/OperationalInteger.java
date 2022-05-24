package com.github.zhuaidadaya.rikaishinikui.handler.universal.operational;

import java.util.function.*;

public class OperationalInteger extends Operational<Integer> {
    private Integer intValue;
    private Consumer<Integer> callback = e -> {};

    public OperationalInteger(Integer base) {
        intValue = base;
    }

    public OperationalInteger(Long base) {
        intValue = Math.toIntExact(base);
    }

    public OperationalInteger() {
        intValue = 0;
    }

    public Integer get() {
        return intValue;
    }

    public Integer set(Integer value) {
        intValue = value;
        callback(intValue);
        return value;
    }

    public OperationalInteger callback(Consumer<Integer> callback) {
        this.callback = callback;
        return this;
    }

    private void callback(Integer IntegerValue) {
        callback.accept(IntegerValue);
    }

    public Integer add() {
        callback(intValue++);
        return intValue;
    }

    public Integer add(Integer value, Integer amplifier) {
        return add(value * amplifier);
    }

    public Integer add(Integer value) {
        intValue += value;
        callback(intValue);
        return intValue;
    }

    public Integer reduce(Integer value, Integer amplifier) {
        return reduce(value * amplifier);
    }

    public Integer reduce(Integer value) {
        intValue -= value;
        callback(intValue);
        return intValue;
    }

    public Integer add(Long value, Integer amplifier) {
        return add(value * amplifier);
    }

    public Integer add(Long value) {
        intValue += value.intValue();
        callback(intValue);
        return intValue;
    }

    public Integer reduce(Long value, Integer amplifier) {
        return reduce(value * amplifier);
    }

    public Integer reduce(Long value) {
        intValue -= value.intValue();
        callback(intValue);
        return intValue;
    }

    public Integer reduce() {
        callback(intValue--);
        return intValue;
    }

    public Integer multiply(Integer value) {
        callback(intValue *= value);
        return intValue;
    }

    public Integer divide(Integer value) {
        callback(intValue /= value);
        return intValue;
    }
}
