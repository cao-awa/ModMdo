package com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust;

import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.*;

public class EntrustParser {
    static final Map<String, String> name = new HashMap<>();

    public static <T> T getNotNull(T target, @NotNull T defaultValue) {
        if (target == null) {
            return defaultValue;
        }
        return target;
    }

    public static <T> T nullRequires(T target, Supplier<T> action) {
        if (target == null) {
            return action.get();
        }
        return null;
    }

    public static <T> T build(Supplier<T> obj) {
        return obj.get();
    }

    public static <T> T create(Supplier<T> action) {
        return action.get();
    }

    public static <T> T tryCreate(ExceptingSupplier<T> action, T defaultValue) {
        try {
            return action.get();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static <T> T trying(ExceptingSupplier<T> action) {
        try {
            return action.get();
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T operation(Supplier<T> target) {
        return target.get();
    }

    public static <T> T tryInstance(Class<T> clazz, ExceptingSupplier<T> target, T defaultValue) {
        try {
            return target.get();
        } catch (Throwable e) {

        }
        return defaultValue;
    }

    public static void main(String[] args) {
        EntrustExecution.trying("", System.out::println);
    }

    public static <T> T operation(T target, Consumer<T> action) {
        action.accept(target);
        return target;
    }

    public static <T> T trying(ExceptingSupplier<T> action, Supplier<T> actionWhenException) {
        try {
            return action.get();
        } catch (Exception e) {
            return actionWhenException.get();
        }
    }

    public static <T> T select(T[] array, int index) {
        return array.length > index ? array[index] : array[array.length - 1];
    }

    public static <T> T select(T[] array, Random random) {
        return array[random.nextInt(array.length)];
    }
}
