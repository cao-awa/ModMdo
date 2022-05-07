package com.github.zhuaidadaya.modmdo.whitelist;

import com.github.zhuaidadaya.modmdo.login.*;
import org.json.*;

import java.util.*;

public class PermanentWhitelist extends Whitelist {
    public PermanentWhitelist(String name, String identifier, UUID uuid) {
        super(name, new LoginRecorde(identifier,uuid, identifier.equals("") ? LoginRecordeType.UUID : LoginRecordeType.IDENTIFIER));
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        json.put("name",getName());
        json.put("unique_id", getIdentifier());
        json.put("uuid", recorde.uuid());
        return json;
    }

    public static PermanentWhitelist build(JSONObject json) {
        return new PermanentWhitelist(json.getString("name"), json.getString("unique_id"), UUID.fromString(json.getString("uuid")));
    }
}
