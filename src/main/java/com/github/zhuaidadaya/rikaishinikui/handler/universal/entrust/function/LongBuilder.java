package com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function;

import java.io.*;

public interface LongBuilder<T> extends Serializable {
    Long build(T target);
}
