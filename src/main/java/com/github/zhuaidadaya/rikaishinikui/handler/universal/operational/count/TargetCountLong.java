package com.github.zhuaidadaya.rikaishinikui.handler.universal.operational.count;

import com.github.zhuaidadaya.rikaishinikui.handler.universal.operational.*;

import java.util.function.*;

public class TargetCountLong<T> extends TargetCount {
    private final T target;
    private final long targetCount;
    private final OperationalLong count;
    private Consumer<T> action;

    public TargetCountLong(T target, long targetCount) {
        this.target = target;
        this.targetCount = targetCount;
        this.count = new OperationalLong(0).callback(this::action);
    }

    private void action(long longValue) {
        if (longValue == targetCount) {
            action.accept(target);
        }
    }

    public TargetCountLong(T target, long targetCount, long count) {
        this.target = target;
        this.targetCount = targetCount;
        this.count = new OperationalLong(count).callback(this::action);
    }

    public T getTarget() {
        return target;
    }

    public long getTargetCount() {
        return targetCount;
    }

    public Consumer<T> getAction() {
        return action;
    }

    public void setAction(Consumer<T> action) {
        this.action = action;
    }

    public OperationalLong get() {
        return count;
    }

    public long getValue() {
        return count.get();
    }

    public long add() {
        return count.add();
    }

    public long add(long value) {
        return count.add(value);
    }

    @Override
    public boolean satisfy() {
        return targetCount == count.get();
    }
}
