package com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function;

import java.io.*;

public abstract class ActionIns<T, R> implements Serializable {
    public abstract R action(T target);
}

