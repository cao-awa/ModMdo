package com.github.zhuaidadaya.modMdo.usr;

import com.github.zhuaidadaya.modMdo.login.token.ClientEncryptionToken;
import com.github.zhuaidadaya.modMdo.storage.Variables;
import org.json.JSONObject;

import java.util.UUID;

public class User {
    private String name;
    private UUID uuid;
    private int level = 1;
    private ClientEncryptionToken clientToken = new ClientEncryptionToken("","","default", Variables.VERSION_ID);
    private boolean exceptToken = true;

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

    public User(String name, String uuid, int level) {
        this.name = name;
        this.uuid = UUID.fromString(uuid);
        this.level = level;
    }

    public User(String name, String uuid, int level, ClientEncryptionToken token) {
        this.name = name;
        this.uuid = UUID.fromString(uuid);
        this.level = level;
        this.clientToken = token;
        exceptToken = false;
    }

    public User(JSONObject json) {
        String name = json.get("name").toString();
        String uuid = json.get("uuid").toString();
        int level = json.getInt("level");

        this.name = name;
        this.uuid = UUID.fromString(uuid);
        this.level = level;

        try {
            JSONObject token = json.getJSONObject("token");
            for(Object o : token.keySet()) {
                JSONObject tokenContent = token.getJSONObject(o.toString());
                this.clientToken = new ClientEncryptionToken(tokenContent.getString("token"), tokenContent.getString("address"), tokenContent.getString("login_type"), tokenContent.getString("modmdo_version"));
            }
        } catch (Exception e) {

        }
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
        try {
            if(exceptToken)
                throw new Exception();
            return new JSONObject().put("name", name).put("uuid", uuid).put("level", level).put("token", clientToken.toJSONObject());
        } catch (Exception e) {
            return new JSONObject().put("name", name).put("uuid", uuid).put("level", level);
        }
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

    public void exceptToken(boolean except) {
        exceptToken = except;
    }
}