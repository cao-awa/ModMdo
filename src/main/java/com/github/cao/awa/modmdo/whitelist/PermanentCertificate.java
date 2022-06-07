package com.github.cao.awa.modmdo.whitelist;

import com.github.cao.awa.modmdo.server.login.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import org.json.*;

import java.util.*;

public class PermanentCertificate extends Certificate {
    public PermanentCertificate(String name, String identifier, UUID uuid) {
        super(name, new LoginRecorde(identifier, uuid, identifier.equals("") ? LoginRecordeType.UUID : LoginRecordeType.IDENTIFIER));
    }

    public static PermanentCertificate build(JSONObject json) {
        UUID uuid = EntrustParser.trying(() -> UUID.fromString(json.getString("uuid")));
        String uniqueId = EntrustParser.trying(() -> json.getString("unique_id"), () -> "");
        return new PermanentCertificate(json.getString("name"), uniqueId, uuid);
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        json.put("type", "permanent");
        json.put("name", getName());
        json.put("unique_id", getIdentifier());
        json.put("uuid", recorde.uuid());
        return json;
    }
}
