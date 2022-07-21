package com.github.zhuaidadaya.rikaishinikui.handler.conductor.string;

import com.github.zhuaidadaya.rikaishinikui.handler.conductor.string.iterator.*;
import org.jetbrains.annotations.*;

import java.util.*;

public record StringTokenizerConductor(StringTokenizer tokenizer) implements Iterable<String> {
    @NotNull
    @Override
    public Iterator<String> iterator() {
        return new StringTokenizerIterator(tokenizer);
    }

    public int size() {
        return tokenizer.countTokens();
    }
}
