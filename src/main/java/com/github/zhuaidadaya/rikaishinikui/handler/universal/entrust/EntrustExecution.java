package com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust;

import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.*;

import java.util.*;
import java.util.function.*;

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

    public static <T> void equalsValue(Supplier<T> target, Supplier<T> tester, Consumer<T> equalsAction, Consumer<T> elseAction) {
        if (tester.get().equals(target.get())) {
            equalsAction.accept(target.get());
        } else {
            elseAction.accept(target.get());
        }
    }

    public static <T> void assertValue(Supplier<T> target, Supplier<T> tester, Consumer<T> equalsAction, Consumer<T> elseAction) {
        if (tester.get() == target.get()) {
            equalsAction.accept(target.get());
        } else {
            elseAction.accept(target.get());
        }
    }

    public static <T> void operation(T target, Consumer<T> action) {
        action.accept(target);
    }

    @SafeVarargs
    public static <T> void order(T target, Consumer<T>... actions) {
        for (Consumer<T> action : actions) {
            action.accept(target);
        }
    }

    public static <T> void trying(ExceptingConsumer<T> action) {
        try {
            action.accept((T) o);
        } catch (Exception e) {

        }
    }

    public static <T> void trying(ExceptingConsumer<T> action, Consumer<T> actionWhenException) {
        try {
            action.accept((T) o);
        } catch (Exception e) {
            actionWhenException.accept((T) o);
        }
    }

    public static <T> void trying(T target, ExceptingConsumer<T> action) {
        try {
            action.accept(target);
        } catch (Exception e) {

        }
    }

    public static <T> void trying(T target, ExceptingConsumer<T> action, Consumer<T> actionWhenException) {
        try {
            action.accept(target);
        } catch (Exception e) {
            actionWhenException.accept(target);
        }
    }

    public static <T> void tryFor(Collection<T> targets, ExceptingConsumer<T> action) {
        if (targets != null) {
            for (T target : targets) {
                try {
                    action.accept(target);
                } catch (Exception e) {

                }
            }
        }
    }

    public static <T> void tryFor(Collection<T> targets, ExceptingConsumer<T> action, Consumer<T> whenException) {
        if (targets != null) {
            for (T target : targets) {
                try {
                    action.accept(target);
                } catch (Exception e) {
                    whenException.accept(target);
                }
            }
        }
    }

    public static <T> void temporary(Temporary action) {
        action.apply();
    }

    public static <T> void tryTemporary(ExceptingTemporary action, Temporary whenException) {
        try {
            action.apply();
        } catch (Exception e) {
            whenException.apply();
        }
    }

    public static <T> void tryTemporary(ExceptingTemporary action, Consumer<Exception> whenException) {
        try {
            action.apply();
        } catch (Exception e) {
            whenException.accept(e);
        }
    }

    public static <T> void tryTemporary(ExceptingTemporary action) {
        try {
            action.apply();
        } catch (Exception e) {

        }
    }

    public static <T> void tryAssertNotNull(T target, ExceptingConsumer<T> action) {
        try {
            if (target != null) {
                action.accept(target);
            }
        } catch (Exception e) {

        }
    }

    public static <T> void tryAssertNotNull(T target, ExceptingConsumer<T> action, Consumer<T> whenException) {
        try {
            if (target != null) {
                action.accept(target);
            }
        } catch (Exception e) {
            whenException.accept(target);
        }
    }

    public static <T> void tryExecuteNull(T target, ExceptingConsumer<T> asNotNull, ExceptingConsumer<T> asNull) {
        try {
            if (target != null) {
                asNotNull.accept(target);
            } else {
                asNull.accept(null);
            }
        } catch (Exception e) {

        }
    }

    public static <T> void tryExecuteNull(T target, ExceptingConsumer<T> asNotNull, ExceptingConsumer<T> asNull, Consumer<T> whenException) {
        try {
            if (target != null) {
                asNotNull.accept(target);
            } else {
                asNull.accept(null);
            }
        } catch (Exception e) {
            whenException.accept(target);
        }
    }

    public static void main(String[] args) {

    }
}

