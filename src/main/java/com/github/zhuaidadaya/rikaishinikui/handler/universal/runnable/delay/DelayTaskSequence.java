package com.github.zhuaidadaya.rikaishinikui.handler.universal.runnable.delay;

import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.annotaions.*;
import it.unimi.dsi.fastutil.objects.*;

@AsyncDelay
public class DelayTaskSequence {
    private final ObjectArrayList<DelayTask> actions = new ObjectArrayList<>();
    private final ObjectArrayList<DelayTask> remove = new ObjectArrayList<>();

    public void submit(Temporary action, int delay) {
        if (delay > 0) {
            actions.add(new DelayTask(action, delay));
        } else {
            action.apply();
        }
    }

    public void tick() {
        actions.parallelStream().filter(DelayTask::tick).forEach(remove::add);
        EntrustExecution.parallelTryFor(remove, actions::remove);
        remove.clear();
    }

    private static class DelayTask {
        private final Temporary action;
        private int delay;

        protected DelayTask(Temporary action, int delay) {
            this.action = action;
            this.delay = delay;
        }

        protected boolean tick() {
            if (--delay == 0) {
                action.apply();
                return true;
            }
            return false;
        }
    }
}
