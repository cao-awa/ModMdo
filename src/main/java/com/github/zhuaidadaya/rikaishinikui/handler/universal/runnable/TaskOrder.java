package com.github.zhuaidadaya.rikaishinikui.handler.universal.runnable;

import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.annotaions.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.operational.count.*;
import it.unimi.dsi.fastutil.objects.*;
import org.jetbrains.annotations.*;

import java.util.function.*;

public class TaskOrder<T> {
    private final @SingleThread @NotNull TargetCountBoolean<Consumer<T>> action;
    private final ObjectArrayList<T> delay = new ObjectArrayList<>();
    private @NotNull Thread thread = new Thread(() -> {});

    public TaskOrder(Consumer<T> action) {
        this.action = new TargetCountBoolean<>(action, true);
    }

    public void call(T target) {
        if (action.satisfy() && !thread.isAlive()) {
            action.reverse();
            call(EntrustParser.thread(() -> {
                action.getTarget().accept(target);
                resolve();
                action.reverse();
            }));
        } else {
            delay.add(target);
        }
    }

    @NotNull
    public Thread getThread() {
        return thread;
    }

    public void call(Thread thread) {
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