package com.github.cao.awa.modmdo.event;

import com.github.cao.awa.modmdo.event.delay.*;
import com.github.cao.awa.modmdo.extra.loader.*;
import com.github.cao.awa.modmdo.utils.times.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.annotaions.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.operational.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.runnable.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.server.*;

import java.io.*;
import java.util.function.*;

@AsyncDelay
public abstract class ModMdoEvent<T extends ModMdoEvent<?>> {
    private final Object2ObjectMap<TaskOrder<T>, Thread> await;
    private final ObjectSet<TaskOrder<T>> actions;
    private final ObjectList<T> delay;
    private final ObjectList<Previously<T>> previously;
    private boolean submitted = false;

    public ModMdoEvent() {
        await = Object2ObjectMaps.synchronize(new Object2ObjectLinkedOpenHashMap<>());
        actions = ObjectSets.synchronize(new ObjectLinkedOpenHashSet<>());
        delay = ObjectLists.synchronize(new ObjectArrayList<>());
        previously = ObjectLists.synchronize(new ObjectArrayList<>());
    }

    public void register(Consumer<T> action, File register) {
        actions.add(new TaskOrder<>(action, "F): " + register.getPath()));
    }

    public void register(Consumer<T> action, ModMdoExtra<?> register, String name) {
        actions.add(new TaskOrder<>(action, "\"" + register.getName() + "\": " + name));
    }

    public ObjectArrayList<String> getRegistered() {
        return EntrustParser.operation(new ObjectArrayList<>(), list -> {
            for (TaskOrder<T> task : actions) {
                list.add(task.getRegister());
            }
        });
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public abstract String synopsis();

    @AsyncDelay
    public void await(Temporary action, int orWait, File register) {
        orWait(new TaskOrder<>(e -> action.apply(), true, "File: \n  " + register.getPath()), orWait);
    }

    public void orWait(TaskOrder<T> order, final int wait) {
        await.put(order, EntrustParser.thread(() -> {
            synchronized (await) {
                EntrustExecution.tryTemporary(() -> {
                    OperationalInteger integer = new OperationalInteger(wait);
                    while (integer.get() > 0) {
                        TimeUtil.coma(10);
                        integer.reduce(10);
                        if (await.get(order).isInterrupted()) {
                            await.remove(order);
                            return;
                        }
                    }
                    if (await.containsKey(order)) {
                        order.call(null);
                        await.remove(order);
                    }
                });
            }
        }));
        await.get(order).start();
    }

    public void skipDelay(T target) {
        if (delay.size() > 0) {
            return;
        }
        submit(target);
        action();
    }

    public void action() {
        action(false);
    }

    @AsyncDelay
    public void action(boolean enforce) {
        for (T target : delay) {
            for (TaskOrder<T> event : actions) {
                if (enforce) {
                    EntrustExecution.trying(target, event::enforce);
                } else {
                    EntrustExecution.trying(target, event::call);
                }
            }
            for (TaskOrder<T> event : await.keySet()) {
                await.get(event).interrupt();
                if (enforce) {
                    EntrustExecution.trying(target, event::enforce);
                } else {
                    EntrustExecution.trying(target, event::call);
                }
                await.remove(event);
            }
            delay.remove(target);
        }

        submitted = false;
    }

    @AsyncDelay
    public void submit(T target) {
        if (previously.size() > 0) {
            target = fuse(previously.get(0), target);
            previously.remove(0);
        }
        delay.add(target);
        submitted = true;
    }

    public abstract T fuse(Previously<T> previously, T delay);

    public int registered() {
        return actions.size();
    }

    @AsyncDelay
    public void immediately(T target, Temporary action) {
        previously(target, action);
        submit(target);
        action();
    }

    @AsyncDelay
    public void previously(T target, Temporary action) {
        previously.add(new Previously<>(target, action));
    }

    @SingleThread
    public void refrainAsync(T target, Temporary action) {
        previously(target, action);
        submit(target);
        action(true);
    }

    @SingleThread
    public void refrainAsync(T target) {
        submit(target);
        action(true);
    }

    public void auto(ModMdoEvent<?> target) {
        if (target.clazz().equals(clazz())) {
            adaptive((T) target);
        }
    }

    public void adaptive(T target) {
        immediately(target);
    }

    @AsyncDelay
    public void immediately(T target) {
        submit(target);
        action();
    }

    public abstract String clazz();

    public abstract String abbreviate();

    public abstract MinecraftServer getServer();
}
