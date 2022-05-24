package com.github.cao.awa.modmdo.whitelist;

import com.github.cao.awa.modmdo.server.login.*;
import org.json.*;

public abstract class Whitelist {
    public final String name;
    public final LoginRecorde recorde;

    public Whitelist(String name, LoginRecorde recorde) {
        this.name = name;
        this.recorde = recorde;
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
}
