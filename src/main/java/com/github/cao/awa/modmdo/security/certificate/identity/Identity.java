package com.github.cao.awa.modmdo.security.certificate.identity;

import com.github.cao.awa.modmdo.annotations.platform.*;
import com.github.cao.awa.modmdo.storage.*;
import org.jetbrains.annotations.*;
import org.json.*;

import java.util.*;

@Server
public class Identity extends Storable {
    private final UUID uuid;
    private String uniqueId;
    private String unidirectionalVerify;

    public Identity(@NotNull String uniqueId, @Nullable UUID uuid, String unidirectionalVerify) {
        this.uniqueId = uniqueId;
        this.uuid = uuid;
        this.unidirectionalVerify = unidirectionalVerify;
    }

    public static Identity build(JSONObject json) {
        return new Identity(
                json.has("unique_id") ? json.getString("unique_id") : "",
                UUID.fromString(json.getString("uuid")),
                json.has("verify") ? json.getString("verify") : ""
        );
    }

    public String getVerify() {
        return this.unidirectionalVerify;
    }

    public void setVerify(String unidirectionalVerify) {
        this.unidirectionalVerify = unidirectionalVerify;
    }

    public String getUniqueId() {
        return this.uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        json.put(
                "unique_id",
                this.uniqueId == null ? "" : this.uniqueId
        );
        json.put(
                "uuid",
                this.uuid
        );
        json.put(
                "verify",
                this.unidirectionalVerify
        );
        return json;
    }
}
