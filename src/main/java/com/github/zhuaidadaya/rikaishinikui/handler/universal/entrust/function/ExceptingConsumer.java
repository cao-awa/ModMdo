package com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function;

import java.util.*;

@FunctionalInterface
public interface ExceptingConsumer<T> {
    void accept(T t) throws Exception;

    default ExceptingConsumer<T> andThen(ExceptingConsumer<? super T> after) throws Exception {
        Objects.requireNonNull(after);
        return (T t) -> {
            accept(t);
            after.accept(t);
        };
    }
}
