package com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function;

import java.io.*;

public interface Action<T, R> extends Serializable {
    R action(T target);
}
