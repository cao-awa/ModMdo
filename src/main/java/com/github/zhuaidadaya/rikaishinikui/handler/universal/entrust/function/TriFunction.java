package com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function;

import java.util.*;
import java.util.function.*;

public interface TriFunction<A, B, C, R> {
    R apply(A a, B b, C c);

    default <V> TriFunction<A, B, C, V> andThen(Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (a, b, c) -> after.apply(apply(a, b, c));
    }
}
