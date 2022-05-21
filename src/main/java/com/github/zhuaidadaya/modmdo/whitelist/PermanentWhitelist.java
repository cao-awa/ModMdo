package com.github.zhuaidadaya.modmdo.whitelist;

import com.github.zhuaidadaya.modmdo.server.login.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import org.json.*;

import java.util.*;

public class PermanentWhitelist extends Whitelist {
    public PermanentWhitelist(String name, String identifier, UUID uuid) {
        super(name, new LoginRecorde(identifier, uuid, identifier.equals("") ? LoginRecordeType.UUID : LoginRecordeType.IDENTIFIER));
    }

    public static PermanentWhitelist build(JSONObject json) {
        UUID uuid = EntrustParser.trying(() -> UUID.fromString(json.getString("uuid")));
        String uniqueId = EntrustParser.trying(() -> json.getString("unique_id"), () -> "");
        return new PermanentWhitelist(json.getString("name"), uniqueId, uuid);
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        json.put("name", getName());
        json.put("unique_id", getIdentifier());
        json.put("uuid", recorde.uuid());
        return json;
    }
}
