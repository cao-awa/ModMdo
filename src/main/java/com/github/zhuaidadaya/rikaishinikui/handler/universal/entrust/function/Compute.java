package com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function;

import java.io.*;

public interface Compute<R, T> extends Serializable {
    R compute(T target);
}
