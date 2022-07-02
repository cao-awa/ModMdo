package com.github.cao.awa.modmdo.enchant;

import com.github.cao.awa.modmdo.storage.*;
import org.json.JSONObject;

public class EnchantmentLevel extends Storable {
    private final short defaultMax;
    private short max;

    public EnchantmentLevel(short defaultMax) {
        this.defaultMax = defaultMax;
        this.max = defaultMax;
    }

    public EnchantmentLevel(short defaultMax, short max) {
        this.defaultMax = defaultMax;
        this.max = max;
    }

    public EnchantmentLevel(JSONObject json) {
        this.defaultMax = (short) json.getInt("default");
        this.max = (short) json.getInt("max");
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        json.put("default", defaultMax);
        json.put("max", max);
        return json;
    }

    public short getDefaultMax() {
        return defaultMax;
    }

    public short getMax() {
        return max;
    }

    public EnchantmentLevel setMax(short max) {
        this.max = max;
        return this;
    }
}
