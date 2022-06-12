package com.github.cao.awa.modmdo.develop.text;

import net.minecraft.text.*;

public record Literal(LiteralText literal) {
    public static Literal literal(String string) {
        return new Literal(new LiteralText(string));
    }

    public MutableText text() {
        return literal;
    }

    public String getString() {
        return literal.asString();
    }
}
