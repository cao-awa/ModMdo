package com.github.zhuaidadaya.rikaishinikui.handler.universal.runnable;

import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.*;
import it.unimi.dsi.fastutil.objects.*;

import java.util.concurrent.*;
import java.util.stream.*;

public class FutureTaskOrder {
    private final ObjectLinkedOpenHashSet<FutureTask> tasks = new ObjectLinkedOpenHashSet<>();
    private final ObjectLinkedOpenHashSet<FutureTask> parallelTasks = new ObjectLinkedOpenHashSet<>();

    public void submit(Temporary action, int waitTicks) {
        submit(action, waitTicks, false);
    }

    public void submit(Temporary action, int waitTicks, boolean parallel) {
        if (waitTicks < 1) {
            submit(action, parallel);
            return;
        }
        if (parallel) {
            parallelTasks.add(new FutureTask(action, waitTicks));
        } else {
            tasks.add(new FutureTask(action, waitTicks));
        }
    }

    public void submit(Temporary action, boolean parallel) {
        if (parallel) {
            CompletableFuture.runAsync(action::apply);
        } else {
            action.apply();
        }
    }

    public void submit(Temporary action) {
        action.apply();
    }

    public void tick() {
        if (tasks.size() > 0) {
            Stream<FutureTask> remove = tasks.stream().filter(FutureTask::tick);
            remove.forEach(tasks::remove);
        }
        if (parallelTasks.size() > 0) {
            Stream<FutureTask> remove = parallelTasks.parallelStream().filter(FutureTask::tick);
            remove.forEach(parallelTasks::remove);
        }
    }

    private static class FutureTask {
        private final Temporary action;
        private int tick;

        public FutureTask(Temporary action, int ticks) {
            this.action = action;
            this.tick = ticks;
        }

        public boolean tick() {
            if (tick-- == 0) {
                action.apply();
                return true;
            }
            return false;
        }
    }
}
