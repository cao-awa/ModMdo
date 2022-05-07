package com.github.zhuaidadaya.modmdo.whitelist;

import com.github.zhuaidadaya.modmdo.login.*;
import org.json.*;

import java.util.*;

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
