package com.github.zhuaidadaya.rikaishinikui.handler.universal.runnable;

import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.*;
import it.unimi.dsi.fastutil.objects.*;

import java.util.stream.*;

public class FutureTaskOrder {
    private final ObjectLinkedOpenHashSet<FutureTask> tasks = new ObjectLinkedOpenHashSet<>();

    public void submit(Temporary action, int waitTicks) {
        if (waitTicks < 1) {
            action.apply();
            return;
        }
        tasks.add(new FutureTask(action, waitTicks));
    }

    public void submit(Temporary action) {
        action.apply();
    }

    public void tick() {
        Stream<FutureTask> remove = tasks.stream().filter(FutureTask::tick);
        remove.forEach(tasks::remove);
    }

    private static class FutureTask {
        private int tick;
        private final Temporary action;

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
