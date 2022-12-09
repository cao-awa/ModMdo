package com.github.cao.awa.modmdo.security.key;

import com.github.cao.awa.modmdo.annotations.platform.*;
import com.github.cao.awa.modmdo.storage.*;
import org.jetbrains.annotations.*;
import org.json.*;

@Client
public class SecureKey extends Storable {
    private @NotNull String privateKey;
    private @NotNull String verifyKey;
    private String serverId;
    private String address;
    private @Nullable String id;

    public SecureKey(@NotNull String privateKey, @NotNull String verifyKey, String address) {
        this.privateKey = privateKey;
        this.address = address;
        this.verifyKey = verifyKey;
    }

    public SecureKey(@NotNull String privateKey, @Nullable String id, @NotNull String verifyKey, String address) {
        this.privateKey = privateKey;
        this.id = id;
        this.verifyKey = verifyKey;
        this.address = address;
    }

    public SecureKey(@NotNull JSONObject json) {
        this.privateKey = json.getString("private-key");
        this.verifyKey = json.getString("verify-key");
        if (json.has("id")) {
            this.id = json.getString("id");
        }
        this.address = json.getString("address");
    }

    public @NotNull String getVerifyKey() {
        return this.verifyKey;
    }

    public void setVerifyKey(@NotNull String verifyKey) {
        this.verifyKey = verifyKey;
    }

    public String getServerId() {
        return this.serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public @NotNull String getPrivateKey() {
        return this.privateKey;
    }

    public void setPrivateKey(@NotNull String privateKey) {
        this.privateKey = privateKey;
    }

    public @Nullable String getId() {
        return this.id;
    }

    public void setId(@Nullable String id) {
        this.id = id;
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        json.put(
                "private-key",
                this.privateKey
        );
        json.put(
                "verify-key",
                this.verifyKey
        );
        if (this.id != null) {
            json.put(
                    "id",
                    this.id
            );
        }
        json.put(
                "address",
                this.address
        );
        return json;
    }

    public boolean hasId() {
        return this.id != null;
    }

    public boolean hasKey() {
        return true;
    }

    public boolean hasAddress() {
        return this.address != null;
    }
}
