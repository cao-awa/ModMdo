package com.github.cao.awa.modmdo.server.login;

import com.alibaba.fastjson2.*;
import com.github.cao.awa.modmdo.annotations.platform.*;
import com.github.cao.awa.modmdo.security.certificate.identity.*;
import com.github.cao.awa.modmdo.storage.*;
import org.jetbrains.annotations.*;

import java.util.*;

@Server
public final class LoginRecorde extends Storable {
    private final @NotNull Identity identity;
    private final @NotNull LoginRecordeType type;

    public LoginRecorde(@NotNull String modmdoUniqueId, @Nullable UUID uuid, String unidirectionalVerify, @NotNull LoginRecordeType type) {
        this.identity = new Identity(
                modmdoUniqueId,
                uuid,
                unidirectionalVerify
        );
        this.type = type;
    }

    public LoginRecorde(@NotNull Identity identity, @NotNull LoginRecordeType type) {
        this.identity = identity;
        this.type = type;
    }

    public static LoginRecorde build(JSONObject json) {
        return new LoginRecorde(
                Identity.build(json.getJSONObject("identity")),
                LoginRecordeType.of(json.getString("type"))
        );
    }

    public String getUniqueId() {
        return this.identity.getUniqueId();
    }

    public UUID getUuid() {
        return this.identity.getUuid();
    }

    public @NotNull LoginRecordeType type() {
        return this.type;
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        json.put(
                "identity",
                this.identity.toJSONObject()
        );
        json.put(
                "type",
                this.type
        );
        return json;
    }

    public @NotNull Identity getIdentity() {
        return this.identity;
    }
}
