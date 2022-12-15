package com.github.cao.awa.modmdo.event.variable.string;

import com.github.cao.awa.modmdo.annotations.*;
import com.github.cao.awa.modmdo.event.variable.*;
import com.alibaba.fastjson2.*;

import java.io.*;

@Auto
public class PersistentString extends ModMdoPersistent<String> {
    private String value;

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        json.put("value", value);
        json.put("instanceof", getClass().getName());
        json.put("name", name);
        return json;
    }

    public String get() {
        return value;
    }

    public PersistentString clone() {
        return new PersistentString().build(null, toJSONObject());
    }

    public PersistentString build(File file, JSONObject json) {
        setFile(file);
        setMeta(json);
        this.value = json.getString("value");
        this.name = json.getString("name");
        return this;
    }
}
