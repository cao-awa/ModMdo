package com.github.zhuaidadaya.modMdo.usr;

import com.github.zhuaidadaya.modMdo.login.token.ClientEncryptionToken;
import it.unimi.dsi.fastutil.objects.ObjectRBTreeSet;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.UUID;

public class User {
    private String name;
    private UUID uuid;
    private int level = 1;
    private ClientEncryptionToken clientToken = null;
    private ObjectRBTreeSet<String> follows = new ObjectRBTreeSet<>();

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

        try {
            JSONArray subs = json.getJSONArray("subs");
            for(Object o : subs)
                this.follows.add(o.toString());
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
        JSONObject json = new JSONObject();
        try {
            json.put("name", name);
            json.put("uuid", uuid);
            json.put("level", level);
            json.put("token", clientToken.toJSONObject());
            json.put("subs", new JSONArray(follows.toArray()));
        } catch (Exception e) {
            json.put("name", name);
            json.put("uuid", uuid);
            json.put("level", level);
            json.put("subs", new JSONArray(follows.toArray()));
        }

        return json;
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

    public User addFollows(String... follows) {
        if(follows != null)
            this.follows.addAll(List.of(follows));
        return this;
    }

    public void removeFollow(String follow) {
        this.follows.remove(follow);
    }

    public void clearFollows() {
        this.follows = new ObjectRBTreeSet<>();
    }

    public boolean isFollow(String... follows) {
        return this.follows.containsAll(List.of(follows));
    }
}
