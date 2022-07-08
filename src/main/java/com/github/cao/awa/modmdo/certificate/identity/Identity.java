package com.github.cao.awa.modmdo.certificate.identity;

import com.github.cao.awa.modmdo.annotations.platform.*;
import com.github.cao.awa.modmdo.storage.*;
import org.json.*;

import java.util.*;

@Server
public class Identity extends Storable {
    private final UUID uuid;
    private String uniqueId;
    private String unidirectionalVerify;
    public Identity(String uniqueId, UUID uuid, String unidirectionalVerify) {
        this.uniqueId = uniqueId;
        this.uuid = uuid;
        this.unidirectionalVerify = unidirectionalVerify;
    }

    public static Identity build(JSONObject json) {
        return new Identity(json.getString("unique_id"), UUID.fromString(json.getString("uuid")), json.has("unidirectional_verify") ? json.getString("unidirectional_verify") : "");
    }

    public String getVerify() {
        return unidirectionalVerify;
    }

    public void setVerify(String unidirectionalVerify) {
        this.unidirectionalVerify = unidirectionalVerify;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        json.put("unique_id", uniqueId);
        json.put("uuid", uuid);
        json.put("unidirectional_verify", unidirectionalVerify);
        return json;
    }
}
