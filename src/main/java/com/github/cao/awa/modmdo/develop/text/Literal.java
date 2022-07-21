package com.github.cao.awa.modmdo.develop.text;

import net.minecraft.text.*;

public final class Literal extends LiteralText {
    public Literal(String text) {
        super(text);
    }

    public static Literal literal(String string) {
        return new Literal(string);
    }

    public String getString() {
        return this.asString();
    }
}
