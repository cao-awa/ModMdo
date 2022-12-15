package com.github.cao.awa.modmdo.event.task;

import com.github.cao.awa.modmdo.event.register.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.annotaions.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.operational.count.*;
import it.unimi.dsi.fastutil.objects.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.*;

@AsyncDelay
public class ModMdoEventTaskOrder<T> {
    private final @SingleThread
    @NotNull TargetCountBoolean<Consumer<T>> action;
    private final List<T> delay = new ObjectArrayList<>();
    private final boolean disposable;
    private final ModMdoEventRegister register;
    private boolean usable = true;
    private boolean noDelay;
    private boolean reuse;
    private @NotNull Thread thread = new Thread(() -> {
    });

    public ModMdoEventTaskOrder(Consumer<T> action, ModMdoEventRegister register) {
        this(action, false, register);
    }

    public ModMdoEventTaskOrder(Consumer<T> action, boolean disposable, ModMdoEventRegister register) {
        this.action = new TargetCountBoolean<>(action, true, true);
        this.disposable = disposable;
        this.register = register;
        thread.setName(register.getName());
    }

    public boolean isReuse() {
        return reuse;
    }

    public void setReuse(boolean reuse) {
        this.reuse = reuse;
    }

    public boolean isNoDelay() {
        return noDelay;
    }

    public ModMdoEventTaskOrder<T> setNoDelay(boolean noDelay) {
        this.noDelay = noDelay;
        return this;
    }

    public ModMdoEventRegister getRegister() {
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
                action.reverse();
            } else {
                thread = new Thread(() -> {
                    action(target);
                    action.reverse();
                });
                thread.start();
            }
        } else {
            if (usable && ! noDelay) {
                delay.add(target);
            }
        }

        if (disposable) {
            usable = false;
        }
    }

    @AsyncDelay
    private void action(T target) {
        EntrustEnvironment.trys(() -> {
            action.getTarget().accept(target);
            resolve();
        });
    }

    @NotNull
    public Thread getThread() {
        return thread;
    }

    public boolean isRunning() {
        return thread.isAlive();
    }

    private void resolve() {
        for (T target : delay) {
            delay.remove(target);
            enforce(target);
        }
    }
}