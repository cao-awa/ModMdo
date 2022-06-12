package com.github.cao.awa.modmdo.develop.text;

import net.minecraft.text.*;

public record Literal(LiteralTextContent literal) {
    public static Literal literal(String string) {
        return new Literal(new LiteralTextContent(string));
    }

    public MutableText text() {
        return MutableText.of(literal);
    }

    public String getString() {
        return literal.string();
    }
}
