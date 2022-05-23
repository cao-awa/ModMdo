package com.github.zhuaidadaya.modmdo.event.trigger.selector;

public enum EntitySelector {
    ALL("all"), SELF("self");

    final String name;

    EntitySelector(String name) {
        this.name = name;
    }

    public static EntitySelector of(String name) {
        return switch (name) {
            case "all" -> ALL;
            default -> SELF;
        };
    }
}
