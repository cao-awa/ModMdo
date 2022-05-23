package com.github.zhuaidadaya.modmdo.event;

import com.github.cao.awa.hyacinth.logging.*;
import com.github.zhuaidadaya.modmdo.event.delay.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.annotaions.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.runnable.*;
import it.unimi.dsi.fastutil.objects.*;

import java.util.function.*;

import static com.github.zhuaidadaya.modmdo.storage.SharedVariables.*;

public abstract class ModMdoEvent<T extends ModMdoEvent<?>> {
    private final ObjectLinkedOpenHashSet<TaskOrder<T>> actions = new ObjectLinkedOpenHashSet<>();
    private final ObjectArrayList<T> delay = new ObjectArrayList<>();
    private final ObjectOpenHashSet<Previously<T>> previously = new ObjectOpenHashSet<>();
    private boolean submitted = false;

    public synchronized void register(Consumer<T> action) {
        actions.add(new TaskOrder<>(action));
    }

    public boolean isSubmitted() {
        return submitted;
    }

    @SingleThread
    public synchronized void immediately(T target) {
        submit(target);
        action();
    }

    public synchronized void action() {
        for (T target : delay) {
            for (TaskOrder<T> event : actions) {
                EntrustExecution.trying(target, event::call);
            }
            delay.remove(target);
        }
        submitted = false;
    }

    @SingleThread
    public synchronized void submit(T target) {
        if (previously.size() > 0) {
            target = fuse(previously.get(0), target);
            previously.remove(0);
        }
        delay.add(target);
        submitted = true;
        if (testing) {
            PrintUtil.messageToTracker(PrintUtil.tacker(Thread.currentThread().getStackTrace(), - 1, target.synopsis()));
        }
    }

    public abstract T fuse(Previously<T> previously, T delay);

    public abstract String synopsis();

    @SingleThread
    public synchronized void immediately(T target, Temporary action) {
        previously(target, action);
        submit(target);
        action();
    }

    @SingleThread
    public synchronized void previously(T target, Temporary action) {
        previously.add(new Previously<>(target, action));
    }

    public abstract String abbreviate();
}
