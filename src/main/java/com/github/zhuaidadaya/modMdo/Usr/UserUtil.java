package com.github.zhuaidadaya.modMdo.Usr;

import org.json.JSONObject;

import java.util.LinkedHashMap;

public class UserUtil {
    private final LinkedHashMap<Object, JSONObject> users = new LinkedHashMap<>();

    public UserUtil(JSONObject json) {
        for(Object o : json.keySet())
            users.put(o.toString(), json.getJSONObject(o.toString()));
    }

    public void put(Object target, JSONObject value) {
        users.put(target, value);
    }

    public JSONObject getJSONObject(Object target) {
        if(users.get(target.toString()) == null)
            throw new IllegalStateException();
        return users.get(target.toString());
    }

    public Object getUserConfig(Object target, Object config) {
        return users.get(target.toString()).get(config.toString()).toString();
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        for(Object o : users.keySet())
            json.put(o.toString(), users.get(o.toString()));
        return json;
    }
}
