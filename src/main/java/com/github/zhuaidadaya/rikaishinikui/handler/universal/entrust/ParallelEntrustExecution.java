package com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust;

import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.*;

import java.util.*;
import java.util.function.*;

public class ParallelEntrustExecution {
    public static <T> void parallelTryFor(Collection<T> targets, ExceptingConsumer<T> action) {
        parallelTryFor(targets, action, ex -> {});
    }

    public static <T> void parallelTryFor(Collection<T> targets, ExceptingConsumer<T> action, Consumer<T> whenException) {
        if (targets != null) {
            targets.parallelStream().forEach(target -> EntrustExecution.tryTemporary(() -> action.accept(target), ex -> whenException.accept(target)));
        }
    }

    public static <T> void parallelTryFor(T[] targets, ExceptingConsumer<T> action) {
        if (targets != null) {
            Arrays.stream(targets).parallel().forEach(target -> EntrustExecution.tryTemporary(() -> action.accept(target)));
        }
    }

    public static <T> void parallelTryFor(T[] targets, ExceptingConsumer<T> action, Consumer<T> whenException) {
        if (targets != null) {
            Arrays.stream(targets).parallel().forEach(target -> EntrustExecution.tryTemporary(() -> action.accept(target), ex -> whenException.accept(target)));
        }
    }

    public static <T> void ensureTryFor(Collection<T> targets, ExceptingConsumer<T> action, ExceptingConsumer<T> whenException) {
        if (targets != null) {
            targets.parallelStream().forEach(target -> {
                EntrustExecution.ensureTrying(target, action, whenException);
            });
        }
    }
}

