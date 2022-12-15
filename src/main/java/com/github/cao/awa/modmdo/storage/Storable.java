package com.github.cao.awa.modmdo.storage;

import com.alibaba.fastjson2.*;

public abstract class Storable {
    public String toString() {
        return toJSONObject().toString();
    }

    public abstract JSONObject toJSONObject();
}
