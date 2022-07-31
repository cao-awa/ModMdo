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

    public static <T> void runThread(T target, Consumer<T> action) {
        new Thread(() -> action.accept(target)).start();
    }

    public static <T> void executeNull(T target, Consumer<T> asNotNull, Consumer<T> asNull) {
        if (target == null) {
            asNull.accept(null);
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
        EntrustExecution.tryTemporary(() -> {
            action.accept((T) o);
        });
    }

    public static <T> void trying(ExceptingConsumer<T> action, Consumer<T> actionWhenException) {
        try {
            action.accept((T) o);
        } catch (Throwable e) {
            actionWhenException.accept((T) o);
        }
    }

    public static <T> void trying(T target, ExceptingConsumer<T> action) {
        EntrustExecution.tryTemporary(() -> action.accept(target));
    }

    public static <T> void trying(T target, ExceptingConsumer<T> action, Consumer<T> actionWhenException) {
        try {
            action.accept(target);
        } catch (Throwable e) {
            actionWhenException.accept(target);
        }
    }

    public static <T> void ensureTrying(T target, ExceptingConsumer<T> action, ExceptingConsumer<T> actionWhenException) {
        trying(target, action, t -> trying(t, actionWhenException));
    }

    public static <T> void tryFor(Collection<T> targets, ExceptingConsumer<T> action) {
        tryFor(targets, action, ex -> {});
    }

    public static <T> void tryFor(ExceptingSupplier<Collection<T>> targets, ExceptingConsumer<T> action) {
        if (targets != null) {
            EntrustExecution.tryTemporary(() -> tryFor(targets.get(), action));
        }
    }

    public static <T> void tryFor(Collection<T> targets, ExceptingConsumer<T> action, Consumer<T> whenException) {
        if (targets != null) {
            for (T target : targets) {
                try {
                    action.accept(target);
                } catch (Throwable e) {
                    whenException.accept(target);
                }
            }
        }
    }

    public static <T> void tryFor(T[] targets, ExceptingConsumer<T> action) {
        if (targets != null) {
            for (T target : targets) {
                EntrustExecution.tryTemporary(() -> action.accept(target));
            }
        }
    }

    public static <T> void tryFor(T[] targets, ExceptingConsumer<T> action, Consumer<T> whenException) {
        if (targets != null) {
            for (T target : targets) {
                try {
                    action.accept(target);
                } catch (Throwable e) {
                    whenException.accept(target);
                }
            }
        }
    }

    public static <T> void ensureTryFor(Collection<T> targets, ExceptingConsumer<T> action, ExceptingConsumer<T> whenException) {
        if (targets != null) {
            for (T target : targets) {
                ensureTrying(target, action, whenException);
            }
        }
    }

    public static <T> void temporary(Temporary action) {
        action.apply();
    }

    public static <T> void tryTemporary(ExceptingTemporary action, Temporary whenException) {
        try {
            action.apply();
        } catch (Throwable e) {
            whenException.apply();
        }
    }

    public static <T> void tryTemporary(ExceptingTemporary action, Consumer<Throwable> whenException) {
        try {
            action.apply();
        } catch (Throwable e) {
            whenException.accept(e);
        }
    }

    public static <T> void tryTemporary(ExceptingTemporary action) {
        try {
            action.apply();
        } catch (Throwable e) {

        }
    }

    public static <T> void tryAssertNotNull(T target, ExceptingConsumer<T> action) {
        EntrustExecution.tryTemporary(() -> {
            if (target != null) {
                action.accept(target);
            }
        });
    }

    public static <T> void tryAssertNotNull(T target, ExceptingConsumer<T> action, Consumer<T> whenException) {
        try {
            if (target != null) {
                action.accept(target);
            }
        } catch (Throwable e) {
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
        } catch (Throwable e) {

        }
    }

    public static <T> void tryExecuteNull(T target, ExceptingConsumer<T> asNotNull, ExceptingConsumer<T> asNull, Consumer<T> whenException) {
        try {
            if (target != null) {
                asNotNull.accept(target);
            } else {
                asNull.accept(null);
            }
        } catch (Throwable e) {
            whenException.accept(target);
        }
    }
}

