package com.github.cao.awa.modmdo.event.variable.integer.operation;

public enum IntegerOperation {
    ADD, REDUCE, MULTIPLY, DIVIDE, SET;

    public static IntegerOperation of(String name) {
        return switch (name) {
            case "add" -> ADD;
            case "reduce" -> REDUCE;
            case "multiply" -> MULTIPLY;
            case "divide" -> DIVIDE;
            case "set" -> SET;
            default -> {throw new IllegalArgumentException("Operation \"" + name + "\" not found");}
        };
    }
}
