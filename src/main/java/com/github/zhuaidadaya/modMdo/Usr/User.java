package com.github.zhuaidadaya.modMdo.usr;

import com.github.zhuaidadaya.modMdo.token.ClientEncryptionToken;
import org.json.JSONObject;

import java.util.UUID;

public class User {
    private String name;
    private UUID uuid;
    private int level = 1;
    private ClientEncryptionToken clientToken;

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

    public User(String name, String uuid,int level) {
        this.name = name;
        this.uuid = UUID.fromString(uuid);
        this.level = level;
    }

    public User(String name, String uuid,int level,ClientEncryptionToken token) {
        this.name = name;
        this.uuid = UUID.fromString(uuid);
        this.level = level;
        this.clientToken = token;
    }

    public User(JSONObject json) {
        String name = json.get("name").toString();
        String uuid = json.get("uuid").toString();
        int level = json.getInt("level");

        this.name = name;
        this.uuid = UUID.fromString(uuid);
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = UUID.fromString(uuid);
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getID() {
        return uuid.toString();
    }

    public JSONObject toJSONObject() {
        return new JSONObject().put("name", name).put("uuid", uuid).put("level", level).put("token", clientToken.getToken());
    }

    public int getLevel() {
        return level;
    }

    public User setLevel(int level) {
        this.level = level;
        return this;
    }

    public ClientEncryptionToken getClientToken() {
        return clientToken;
    }

    public User setClientToken(ClientEncryptionToken token) {
        this.clientToken = token;
        return this;
    }
}
