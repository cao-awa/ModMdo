package com.github.zhuaidadaya.rikaishinikui.handler.universal.operational;

import java.util.concurrent.atomic.*;

public class OperationalLong {
    private long longValue;

    public OperationalLong(long base) {
        longValue = base;
    }

    public OperationalLong() {
        longValue = 0L;
    }

    public long get() {
        return longValue;
    }

    public long add() {
        return ++longValue;
    }

    public long add(long value) {
        return longValue += value;
    }

    public long reduce() {
        return --longValue;
    }

    public long reduce(long value) {
        return longValue -= value;
    }

    public long set(long value) {
        longValue = value;
        return value;
    }
}
