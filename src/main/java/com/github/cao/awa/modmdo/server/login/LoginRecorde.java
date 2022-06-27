package com.github.cao.awa.modmdo.server.login;

import com.github.cao.awa.modmdo.certificate.identity.*;
import com.github.cao.awa.modmdo.storage.*;
import org.json.*;

import java.util.*;

public final class LoginRecorde extends Storable {
    private final Identity identity;
    private final LoginRecordeType type;

    public LoginRecorde(String modmdoUniqueId, UUID uuid, String unidirectionalVerify, LoginRecordeType type) {
        this.identity = new Identity(modmdoUniqueId, uuid, unidirectionalVerify);
        this.type = type;
    }

    public LoginRecorde(Identity identity, LoginRecordeType type) {
        this.identity = identity;
        this.type = type;
    }

    public static LoginRecorde build(JSONObject json) {
        return new LoginRecorde(Identity.build(json), LoginRecordeType.of(json.getString("type")));
    }

    public String getUniqueId() {
        return identity.getUniqueId();
    }

    public UUID getUuid() {
        return identity.getUuid();
    }

    public LoginRecordeType type() {
        return type;
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        json.put("identity", identity.toJSONObject());
        json.put("type", type);
        return json;
    }

    public Identity getIdentity() {
        return identity;
    }
}
