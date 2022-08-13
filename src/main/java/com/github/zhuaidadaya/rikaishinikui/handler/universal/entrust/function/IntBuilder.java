package com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function;

import java.io.*;

public interface IntBuilder<T> extends Serializable {
    Integer build(T target);
}
