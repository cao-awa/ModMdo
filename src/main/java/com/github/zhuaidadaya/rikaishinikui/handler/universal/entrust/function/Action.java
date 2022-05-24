package com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function;

public interface Action<T, R> {
    R action(T target);
}
