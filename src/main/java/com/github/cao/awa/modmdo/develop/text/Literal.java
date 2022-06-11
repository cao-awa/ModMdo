package com.github.cao.awa.modmdo.develop.text;

import net.minecraft.text.*;

public record Literal(LiteralText text) {
    public static Literal literal(String string) {
        return new Literal(new LiteralText(string));
    }

    public String getString() {
        return text.asString();
    }
}
