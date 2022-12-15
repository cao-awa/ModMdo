package com.github.cao.awa.modmdo.event;

import com.github.cao.awa.modmdo.event.delay.*;
import com.github.cao.awa.modmdo.event.register.*;
import com.github.cao.awa.modmdo.event.task.*;
import com.github.cao.awa.modmdo.extra.loader.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.utils.times.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.annotaions.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.operational.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.server.*;

import java.io.*;
import java.util.*;
import java.util.function.*;

@AsyncDelay
public abstract class ModMdoEvent<T extends ModMdoEvent<?>> {
    private final Map<ModMdoEventTaskOrder<T>, Thread> await;
    private final Set<ModMdoEventTaskOrder<T>> actions;
    private final List<T> delay;
    private final List<Previously<T>> previously;
    private boolean submitted = false;

    public ModMdoEvent() {
        this.await = Object2ObjectMaps.synchronize(new Object2ObjectLinkedOpenHashMap<>());
        this.actions = ObjectSets.synchronize(new ObjectLinkedOpenHashSet<>());
        this.delay = ObjectLists.synchronize(new ObjectArrayList<>());
        this.previously = ObjectLists.synchronize(new ObjectArrayList<>());
    }

    public void register(Consumer<T> action, ModMdoExtra<?> register, File file) {
        this.actions.add(new ModMdoEventTaskOrder<>(
                action,
                new ModMdoEventRegister(
                        register,
                        file
                )
        ));
    }

    public void register(Consumer<T> action, ModMdoExtra<?> register, String name) {
        this.actions.add(new ModMdoEventTaskOrder<>(
                action,
                new ModMdoEventRegister(
                        register,
                        "'" + register.getName() + "' - " + name
                )
        ));
    }

    public ObjectArrayList<ModMdoEventRegister> getRegistered() {
        return EntrustEnvironment.operation(
                new ObjectArrayList<>(),
                list -> {
                    for (ModMdoEventTaskOrder<T> task : this.actions) {
                        list.add(task.getRegister());
                    }
                }
        );
    }

    public boolean isSubmitted() {
        return this.submitted;
    }

    @AsyncDelay
    public void await(Temporary action, int orWait, File file) {
        orWait(
                new ModMdoEventTaskOrder<>(
                        e -> action.apply(),
                        true,
                        new ModMdoEventRegister(
                                SharedVariables.extras.getExtra(
                                        ModMdo.class,
                                        SharedVariables.EXTRA_ID
                                ),
                                file.getName()
                        )
                ),
                orWait
        );
    }

    public abstract String getName();

    public void orWait(ModMdoEventTaskOrder<T> order, final int wait) {
        this.await.put(
                order,
                EntrustEnvironment.thread(() -> {
                    synchronized (this.await) {
                        EntrustEnvironment.trys(() -> {
                            OperationalInteger integer = new OperationalInteger(wait);
                            while (integer.get() > 0) {
                                TimeUtil.coma(10);
                                integer.reduce(10);
                                if (this.await.get(order)
                                         .isInterrupted()) {
                                    this.await.remove(order);
                                    return;
                                }
                            }
                            if (this.await.containsKey(order)) {
                                order.call(null);
                                this.await.remove(order);
                            }
                        });
                    }
                })
        );
        this.await.get(order)
             .start();
    }

    public void skipDelay(T target) {
        if (this.delay.size() > 0) {
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
        for (T target : this.delay) {
            for (ModMdoEventTaskOrder<T> event : this.actions) {
                EntrustEnvironment.operation(
                        target,
                        enforce ? event::enforce : event::call
                );
            }
            for (ModMdoEventTaskOrder<T> event : this.await.keySet()) {
                this.await.get(event)
                     .interrupt();
                EntrustEnvironment.operation(
                        target,
                        enforce ? event::enforce : event::call
                );
                this.await.remove(event);
            }
            this.delay.remove(target);
        }

        this.submitted = false;
    }

    @AsyncDelay
    public void submit(T target) {
        if (this.previously.size() > 0) {
            target = fuse(
                    this.previously.get(0),
                    target
            );
            this.previously.remove(0);
        }
        this.delay.add(target);
        this.submitted = true;
    }

    public abstract T fuse(Previously<T> previously, T delay);

    public int registered() {
        return this.actions.size();
    }

    @AsyncDelay
    public void immediately(T target, Temporary action) {
        previously(
                target,
                action
        );
        submit(target);
        action();
    }

    @AsyncDelay
    public void previously(T target, Temporary action) {
        previously.add(new Previously<>(
                target,
                action
        ));
    }

    @SingleThread
    public void refrainAsync(T target, Temporary action) {
        previously(
                target,
                action
        );
        submit(target);
        action(true);
    }

    @SingleThread
    public void refrainAsync(T target) {
        submit(target);
        action(true);
    }

    public void auto(ModMdoEvent<?> target) {
        if (Objects.equals(
                target.clazz(),
                clazz()
        )) {
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
