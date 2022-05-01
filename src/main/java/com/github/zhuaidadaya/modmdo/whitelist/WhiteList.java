package com.github.zhuaidadaya.modmdo.whitelist;

import org.json.*;

public record WhiteList(String name, String identifier) {
    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        json.put("name",name);
        json.put("id", identifier);
        return json;
    }

    public static WhiteList build(JSONObject json) {
        return new WhiteList(json.getString("name"), json.getString("id"));
    }
}
