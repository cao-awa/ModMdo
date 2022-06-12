package com.github.zhuaidadaya.rikaishinikui.handler.universal.runnable;

import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.annotaions.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.operational.count.*;
import it.unimi.dsi.fastutil.objects.*;
import org.jetbrains.annotations.*;

import java.util.function.*;

@AsyncDelay
public class TaskOrder<T> {
    private final @SingleThread
    @NotNull TargetCountBoolean<Consumer<T>> action;
    private final ObjectArrayList<T> delay = new ObjectArrayList<>();
    private final boolean disposable;
    private final String register;
    private boolean usable = true;
    private @NotNull Thread thread = new Thread(() -> {
    });

    public TaskOrder(Consumer<T> action, String register) {
        this(action, false, register);
    }

    public TaskOrder(Consumer<T> action, boolean disposable, String register) {
        this.action = new TargetCountBoolean<>(action, true, true);
        this.disposable = disposable;
        this.register = register;
    }

    public String getRegister() {
        return register;
    }

    @AsyncDelay
    public void call(T target) {
        action(false, target);
    }

    @AsyncDelay
    public void enforce(T target) {
        action(true, target);
    }

    @AsyncDelay
    private void action(boolean enforce, T target) {
        if (action.satisfy() && ! thread.isAlive() && usable) {
            action.reverse();
            if (enforce) {
                action(target);
            } else {
                call(EntrustParser.thread(() -> action(target)));
            }
            action.reverse();
        } else {
            if (usable) {
                delay.add(target);
            }
        }

        if (disposable) {
            usable = false;
        }
    }

    @AsyncDelay
    private void action(T target) {
        EntrustExecution.tryTemporary(() -> {
            action.getTarget().accept(target);
            resolve();
        });
    }

    @NotNull
    public Thread getThread() {
        return thread;
    }

    private void call(Thread thread) {
        this.thread = thread;
        thread.start();
    }

    private void resolve() {
        for (T target : delay) {
            delay.remove(target);
            call(target);
        }
    }
}