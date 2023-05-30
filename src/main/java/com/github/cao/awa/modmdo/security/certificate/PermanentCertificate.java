package com.github.cao.awa.modmdo.security.certificate;

import com.alibaba.fastjson2.*;
import com.github.cao.awa.modmdo.annotations.platform.*;
import com.github.cao.awa.modmdo.server.login.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import org.jetbrains.annotations.*;

import java.util.*;

@Server
public class PermanentCertificate extends Certificate {
    public PermanentCertificate(@NotNull String name, @NotNull String identifier, UUID uuid, String unidirectionalVerify) {
        super(
                name,
                new LoginRecorde(
                        identifier,
                        uuid,
                        unidirectionalVerify,
                        identifier.equals("") ?
                        LoginRecordeType.UUID :
                        uuid.toString()
                            .equals("") ? LoginRecordeType.IDENTIFIER : LoginRecordeType.MULTIPLE
                )
        );
    }

    public PermanentCertificate(@NotNull String name, LoginRecorde recorde) {
        super(
                name,
                recorde
        );
    }

    public static PermanentCertificate build(JSONObject json) {
        return EntrustEnvironment.trys(() -> new PermanentCertificate(
                json.getString("name"),
                LoginRecorde.build(json.getJSONObject("recorder"))
        ));
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        json.put(
                "recorder",
                this.recorde.toJSONObject()
        );
        json.put(
                "type",
                "permanent"
        );
        json.put(
                "name",
                this.getName()
        );
        return json;
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
