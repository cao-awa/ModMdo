package com.github.cao.awa.modmdo.security.key;

import com.github.cao.awa.modmdo.storage.*;
import org.jetbrains.annotations.*;
import org.json.*;

public class SecureKey extends Storable {
    private @NotNull String privateKey;
    private String serverId;
    private String address;
    private @Nullable String id;

    public SecureKey(@NotNull String privateKey, String address) {
        this.privateKey = privateKey;
        this.address = address;
    }

    public SecureKey(@NotNull String privateKey, @Nullable String id, String address) {
        this.privateKey = privateKey;
        this.id = id;
        this.address = address;
    }

    public SecureKey(@NotNull JSONObject json) {
        this.privateKey = json.getString("private-key");
        if (json.has("id")) {
            this.id = json.getString("id");
        }
        this.address = json.getString("address");
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public @NotNull String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(@NotNull String privateKey) {
        this.privateKey = privateKey;
    }

    public @Nullable String getId() {
        return id;
    }

    public void setId(@Nullable String id) {
        this.id = id;
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        json.put("private-key", privateKey);
        if (id != null) {
            json.put("id", id);
        }
        json.put("address", address);
        return json;
    }

    public boolean hasId() {
        return id != null;
    }

    public boolean hasKey() {
        return true;
    }

    public boolean hasAddress() {
        return address != null;
    }
}
