package com.github.zhuaidadaya.rikaishinikui.handler.entrust;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class EntrustParser {
    public static <T> T getNotNull(T target, @NotNull T defaultValue) {
        if (target == null) {
            return defaultValue;
        }
        return target;
    }

    public static <T> T build(Supplier<T> obj) {
        return obj.get();
    }
}
