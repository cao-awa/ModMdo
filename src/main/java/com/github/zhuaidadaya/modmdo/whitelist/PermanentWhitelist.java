package com.github.zhuaidadaya.modmdo.whitelist;

import org.json.*;

public class PermanentWhitelist extends Whitelist {
    public PermanentWhitelist(String name, String identifier) {
        super(name, identifier);
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        json.put("name",getName());
        json.put("id", getIdentifier());
        return json;
    }

    public static PermanentWhitelist build(JSONObject json) {
        return new PermanentWhitelist(json.getString("name"), json.getString("id"));
    }
}
