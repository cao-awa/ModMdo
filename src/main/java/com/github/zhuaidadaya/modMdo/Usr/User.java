package com.github.zhuaidadaya.modMdo.Usr;

import org.json.JSONObject;

import java.util.UUID;

public class User {
    private String name;
    private UUID uuid;

    public User() {
    }

    public User(String name) {
        this.name = name;
    }

    public User(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    public User(String name, String uuid) {
        this.name = name;
        this.uuid = UUID.fromString(uuid);
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getID() {
        return uuid.toString();
    }

    public User setUserConfigFromJson(JSONObject json) {
        String name = json.get("name").toString();
        String uuid = json.get("uuid").toString();

        this.name = name;
        this.uuid = UUID.fromString(uuid);

        return this;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setUuid(String uuid) {
        this.uuid = UUID.fromString(uuid);
    }
    
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}
