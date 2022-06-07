package com.github.cao.awa.modmdo.whitelist;

import com.github.cao.awa.modmdo.server.login.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import org.json.*;

public abstract class Certificate {
    public final String name;
    public final LoginRecorde recorde;
    public String lastLanguage = "en_us";

    public Certificate(String name, LoginRecorde recorde) {
        this.name = name;
        this.recorde = recorde;
    }

    public String getLastLanguage() {
        return lastLanguage;
    }

    public void setLastLanguage(String lastLanguage) {
        this.lastLanguage = lastLanguage;
    }

    public String getName() {
        return name;
    }

    public String getIdentifier() {
        return recorde.modmdoUniqueId();
    }

    public abstract JSONObject toJSONObject();

    public LoginRecorde getRecorde() {
        return recorde;
    }

    public static Certificate build(JSONObject json) {
        return EntrustParser.trying(() -> {
            String type = json.getString("type");
            if (type.equals("temporary")) {
                return TemporaryCertificate.build(json);
            } else {
                return PermanentCertificate.build(json);
            }
        }, ex -> {
            ex.printStackTrace();
            return null;
        });
    }
}
