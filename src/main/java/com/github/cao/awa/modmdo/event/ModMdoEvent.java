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
    private final Object2ObjectLinkedOpenHashMap<TaskOrder<T>, Thread> await = new Object2ObjectLinkedOpenHashMap<>();
    private final ObjectLinkedOpenHashSet<TaskOrder<T>> actions = new ObjectLinkedOpenHashSet<>();
    private final ObjectArrayList<T> delay = new ObjectArrayList<>();
    private final ObjectArrayList<Previously<T>> previously = new ObjectArrayList<>();
    private boolean submitted = false;

    public synchronized void register(Consumer<T> action, File register) {
        actions.add(new TaskOrder<>(action, "F): " + register.getPath()));
    }

    public synchronized void register(Consumer<T> action, ModMdoExtra<?> register, String name) {
        actions.add(new TaskOrder<>(action, "\"" + register.getName() + "\": " + name));
    }

    public ObjectArrayList<String> getRegistered() {
        return EntrustParser.operation(new ObjectArrayList<>(), list -> actions.parallelStream().forEach(task -> list.add(task.getRegister())));
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
                    order.call(null);
                    await.remove(order);
                });
            }
        }));
        await.get(order).start();
    }

    public synchronized void skipDelay(T target) {
        if (delay.size() > 0) {
            return;
        }
        submit(target);
        action();
    }

    public synchronized void action() {
        action(false);
    }

    @AsyncDelay
    public synchronized void action(boolean enforce) {
        delay.parallelStream().forEach(target -> {
            actions.parallelStream().forEach(enforce ? eventEnforce -> {
                EntrustExecution.trying(target, eventEnforce::enforce);
            } : eventCall -> {
                EntrustExecution.trying(target, eventCall::call);
            });
            await.keySet().parallelStream().forEach(enforce ? eventEnforce -> {
                await.get(eventEnforce).interrupt();
                EntrustExecution.trying(target, eventEnforce::enforce);
                await.remove(eventEnforce);
            } : eventCall -> {
                await.get(eventCall).interrupt();
                EntrustExecution.trying(target, eventCall::call);
                await.remove(eventCall);
            });
            delay.remove(target);
        });

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
    }

    public abstract T fuse(Previously<T> previously, T delay);

    public int registered() {
        return actions.size();
    }

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
    public synchronized void immediately(T target) {
        submit(target);
        action();
    }

    public abstract String clazz();

    public abstract String abbreviate();

    public abstract MinecraftServer getServer();
}
