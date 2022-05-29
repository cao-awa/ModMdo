package com.github.cao.awa.modmdo.event.trigger.selector.entity;

public enum EntitySelectorType {
    ALL("all"), SELF("self"), APPOINT("appoint");

    final String name;

    EntitySelectorType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static EntitySelectorType of(String name) {
        return switch (name) {
            case "all" -> ALL;
            case "appoint" -> APPOINT;
            default -> SELF;
        };
    }
}
