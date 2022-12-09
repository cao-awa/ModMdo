package com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function;

import java.util.*;

@FunctionalInterface
public interface FourConsumer<A, B, C, D> {
    default FourConsumer<A, B, C, D> andThen(FourConsumer<? super A, ? super B, ? super C, ? super D> after) throws Exception {
        Objects.requireNonNull(after);
        return (a, b, c, d) -> {
            accept(
                    a,
                    b,
                    c,
                    d
            );
            after.accept(
                    a,
                    b,
                    c,
                    d
            );
        };
    }

    void accept(A a, B b, C c, D d);
}
