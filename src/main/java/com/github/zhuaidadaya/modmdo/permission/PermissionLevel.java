package com.github.zhuaidadaya.modmdo.permission;

import java.util.Locale;

public enum PermissionLevel {
    ALL("All", 0), OPS("Ops", 1), UNABLE("Unable", - 1);

    final String name;
    final int id;

    PermissionLevel(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getFormat() {
        return name.toLowerCase(Locale.ROOT);
    }
}
