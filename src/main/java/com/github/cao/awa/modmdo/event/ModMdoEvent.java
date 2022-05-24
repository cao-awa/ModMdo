package com.github.cao.awa.modmdo.event;

import com.github.cao.awa.hyacinth.logging.*;
import com.github.cao.awa.modmdo.event.delay.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.annotaions.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.runnable.*;
import it.unimi.dsi.fastutil.objects.*;

import java.util.function.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@AsyncDelay
public abstract class ModMdoEvent<T extends ModMdoEvent<?>> {
    private final ObjectLinkedOpenHashSet<TaskOrder<T>> actions = new ObjectLinkedOpenHashSet<>();
    private final ObjectArrayList<T> delay = new ObjectArrayList<>();
    private final ObjectArrayList<Previously<T>> previously = new ObjectArrayList<>();
    private boolean submitted = false;

    public synchronized void register(Consumer<T> action) {
        actions.add(new TaskOrder<>(action));
    }

    public boolean isSubmitted() {
        return submitted;
    }

    @AsyncDelay
    public synchronized void immediately(T target) {
        submit(target);
        action();
    }

    @AsyncDelay
    public synchronized void action() {
        for (T target : delay) {
            for (TaskOrder<T> event : actions) {
                EntrustExecution.trying(target, event::call);
            }
            delay.remove(target);
        }
        submitted = false;
    }

    @AsyncDelay
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

    public synchronized void skipDelay(T target) {
        if (delay.size() > 0) {
            return;
        }
        submit(target);
        action();
    }

    public abstract T fuse(Previously<T> previously, T delay);

    public abstract String synopsis();

    @AsyncDelay
    public synchronized void immediately(T target, Temporary action) {
        previously(target, action);
        submit(target);
        action();
    }

    @AsyncDelay
    public synchronized void previously(T target, Temporary action) {
        previously.add(new Previously<>(target, action));
    }

    public abstract String abbreviate();

    public abstract String clazz();
}
