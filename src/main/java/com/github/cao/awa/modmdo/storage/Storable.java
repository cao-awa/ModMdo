package com.github.cao.awa.modmdo.storage;

import org.json.*;

public abstract class Storable {
    public String toString() {
        return toJSONObject().toString();
    }

    public abstract JSONObject toJSONObject();

    public void revert(JSONObject json) {

    }
}
