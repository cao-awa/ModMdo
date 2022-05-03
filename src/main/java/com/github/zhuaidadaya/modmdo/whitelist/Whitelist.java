package com.github.zhuaidadaya.modmdo.whitelist;

import org.json.*;

public abstract class Whitelist {
    private final String name;
    private final String identifier;

    public String getName() {
        return name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Whitelist(String name, String identifier) {
        this.name = name;
        this.identifier = identifier;
    }

    public abstract JSONObject toJSONObject();
}
