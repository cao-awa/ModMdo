package com.github.cao.awa.modmdo.event.trigger.selector;

public enum EntitySelectorType {
    ALL("all"), SELF("self");

    final String name;

    EntitySelectorType(String name) {
        this.name = name;
    }

    public static EntitySelectorType of(String name) {
        return switch (name) {
            case "all" -> ALL;
            default -> SELF;
        };
    }
}
