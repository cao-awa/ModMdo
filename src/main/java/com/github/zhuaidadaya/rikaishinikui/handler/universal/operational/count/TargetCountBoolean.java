package com.github.zhuaidadaya.rikaishinikui.handler.universal.operational.count;

import com.github.zhuaidadaya.rikaishinikui.handler.universal.operational.*;

import java.util.function.*;

public class TargetCountBoolean<T> extends TargetCount {
    private final T target;
    private final boolean targetCount;
    private final OperationalBoolean count;
    private Consumer<T> action;

    public TargetCountBoolean(T target, boolean targetCount) {
        this.target = target;
        this.targetCount = targetCount;
        this.count = new OperationalBoolean(false).callback(this::action);
    }

    private void action(boolean longValue) {
        if (longValue == targetCount) {
            action.accept(target);
        }
    }

    public TargetCountBoolean(T target, boolean targetCount, boolean base) {
        this.target = target;
        this.targetCount = targetCount;
        this.count = new OperationalBoolean(base).callback(this::action);
    }

    public T getTarget() {
        return target;
    }

    public boolean getTargetValue() {
        return targetCount;
    }

    public Consumer<T> getAction() {
        return action;
    }

    public void setAction(Consumer<T> action) {
        this.action = action;
    }

    public OperationalBoolean get() {
        return count;
    }

    public boolean getValue() {
        return count.get();
    }

    public boolean reverse() {
        return count.reverse();
    }

    public boolean satisfy() {
        return targetCount && count.get();
    }
}
