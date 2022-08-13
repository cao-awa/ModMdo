package com.github.cao.awa.shilohrien.databse.increment;

import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.*;

public class MainKeyActor<T> {
    private final MainKeyTactics<T, T> mainKeyTactics;

    public MainKeyActor() {
        this.mainKeyTactics = new KeepMainKeyTactics<>();
    }

    public MainKeyActor(MainKeyTactics<T, T> mainKeyTactics) {
        this.mainKeyTactics = mainKeyTactics;
    }

    public MainKeyActor(MainKeyTactics<T, T> mainKeyTactics, T mainKey) {
        this.mainKeyTactics = mainKeyTactics;
        mainKeyTactics.setKey(mainKey);
    }

    public T action() {
        return mainKeyTactics.generateKey();
    }

    public static final class IntegerMainKeyTactics extends MainKeyTactics<Integer, Integer> {
        public IntegerMainKeyTactics() {
            super();
        }

        public IntegerMainKeyTactics(Integer key) {
            super(key);
        }

        @Override
        public Integer action(Integer target) {
            return target + 1;
        }
    }

    public static final class KeepMainKeyTactics<T> extends MainKeyTactics<T, T> {
        public KeepMainKeyTactics() {
            super();
        }

        public KeepMainKeyTactics(T key) {
            super(key);
        }

        @Override
        public T action(T target) {
            return target;
        }
    }

    public abstract static class MainKeyTactics<L, R> extends ActionIns<L, R> {
        private L key;

        public MainKeyTactics(L key) {
            this.key = key;
        }

        public MainKeyTactics() {

        }

        public L getKey() {
            return key;
        }

        public MainKeyTactics<L, R> setKey(L key) {
            this.key = key;
            return this;
        }

        public R generateKey() {
            return action(key);
        }
    }
}
