package com.github.cao.awa.modmdo.server.login;

import com.github.cao.awa.modmdo.storage.*;
import org.json.*;

import java.util.*;

public final class LoginRecorde extends Storable {
    private final String modmdoUniqueId;
    private final UUID uuid;
    private final LoginRecordeType type;

    public LoginRecorde(String modmdoUniqueId, UUID uuid, LoginRecordeType type) {
        this.modmdoUniqueId = modmdoUniqueId;
        this.uuid = uuid;
        this.type = type;
    }

    public static LoginRecorde build(JSONObject json) {
        return new LoginRecorde(json.getString("unique_id"), UUID.fromString(json.getString("uuid")), LoginRecordeType.of(json.getString("type")));
    }

    public String modmdoUniqueId() {
        return modmdoUniqueId;
    }

    public UUID uuid() {
        return uuid;
    }

    public LoginRecordeType type() {
        return type;
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        json.put("type", type);
        json.put("unique_id", modmdoUniqueId);
        json.put("uuid", uuid);
        return json;
    }
}
