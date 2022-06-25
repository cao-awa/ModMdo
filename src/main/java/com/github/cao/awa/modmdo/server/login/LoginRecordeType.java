package com.github.cao.awa.modmdo.server.login;

public enum LoginRecordeType {
    IDENTIFIER, UUID, TEMPORARY, MULTIPLE;

    public static LoginRecordeType of(String name) {
        return switch (name.toLowerCase()) {
            case "identifier" -> IDENTIFIER;
            case "uuid" -> UUID;
            case "temporary" -> TEMPORARY;
            case "multiple" -> MULTIPLE;
            default -> throw new IllegalArgumentException("Cannot found target type \"" + name + "\"");
        };
    }
}
