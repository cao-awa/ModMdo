package com.github.zhuaidadaya.rikaishinikui.handler.universal.runnable.delay;

import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.annotaions.*;
import it.unimi.dsi.fastutil.objects.*;

@SingleThread
public class InformTask {
    private final Object2ObjectOpenHashMap<String, DelayTask> actions = new Object2ObjectOpenHashMap<>();

    public void submit(String name, Temporary action) {
        actions.put(name, new DelayTask(action));
    }

    public void tick() {
        actions.values().forEach(DelayTask::tick);
        actions.clear();
    }

    private record DelayTask(Temporary action) {
        private void tick() {
            action.apply();
        }
    }
}
