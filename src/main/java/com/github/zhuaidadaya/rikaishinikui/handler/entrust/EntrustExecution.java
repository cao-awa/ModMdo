package com.github.zhuaidadaya.rikaishinikui.handler.entrust;

import org.apache.logging.log4j.core.appender.rolling.action.IfNot;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class EntrustExecution {
    private static final Object o = new Object();

    public static <T> void notNull(T target, Consumer<T> action) {
        if (target != null) {
            action.accept(target);
        }
    }

    public static <T> void nullRequires(T target, Consumer<T> action) {
        if (target == null) {
            action.accept(null);
        }
    }

    public static <T> void executeNull(T target, Consumer<T> asNotNull, Consumer<T> asNull) {
        if (target == null) {
            asNull.accept((T) o);
        } else {
            asNotNull.accept(target);
        }
    }

    public static <T> void before(T target, Consumer<T> first, Consumer<T> before) {
        first.accept(target);
        before.accept(target);
    }

    public static <T> void assertValue(T target, Supplier<T> tester, Consumer<T> equalsAction, Consumer<T> elseAction) {
        if (tester.get() == target) {
            equalsAction.accept(tester.get());
        } else {
            elseAction.accept(tester.get());
        }
    }

    public static <T> void equalsValue(T target, Supplier<T> tester, Consumer<T> equalsAction, Consumer<T> elseAction) {
        if (tester.get().equals(target)) {
            equalsAction.accept(tester.get());
        } else {
            elseAction.accept(tester.get());
        }
    }

    @SafeVarargs
    public static <T> void order(T target, Consumer<T>... actions) {
        for (Consumer<T> action : actions) {
            action.accept(target);
        }
    }

    public static void main(String[] args) {

    }
}
