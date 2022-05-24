package com.github.cao.awa.modmdo.utils.times;

public enum TimeType {
    ALL("all"), AS_DAY("day"),AS_MONTH("month"),AS_YEAR("year"),AS_CLOCK("clock"),AS_MINUTE("minute"),AS_SECOND("second"),LOG("LOG"),LONG_LOG("LONG_LOG");

    private final String  value;

    TimeType(final String typeValue) {
        value = typeValue;
    }

    public String  getValue() {
        return value;
    }
}