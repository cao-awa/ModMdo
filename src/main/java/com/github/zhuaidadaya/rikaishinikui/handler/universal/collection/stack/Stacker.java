package com.github.zhuaidadaya.rikaishinikui.handler.universal.collection.stack;

import java.util.*;
import java.util.function.*;

public class Stacker<T> extends Stack<T> {
    public Stacker() {

    }

    public void popEach(Consumer<T> action) {
        while (hasElements()) {
            action.accept(pop());
        }
    }

    public boolean hasElements() {
        return !this.isEmpty();
    }
}
