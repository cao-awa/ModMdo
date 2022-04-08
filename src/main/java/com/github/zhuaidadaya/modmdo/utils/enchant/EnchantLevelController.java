package com.github.zhuaidadaya.modmdo.utils.enchant;

import com.github.zhuaidadaya.modmdo.reads.FileReads;
import com.github.zhuaidadaya.modmdo.resourceLoader.Resource;
import com.github.zhuaidadaya.modmdo.resourceLoader.Resources;
import it.unimi.dsi.fastutil.ints.Int2ObjectRBTreeMap;
import net.minecraft.util.Identifier;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class EnchantLevelController {
    private final Int2ObjectRBTreeMap<EnchantmentLevel> enchantmentsMaxLevel = new Int2ObjectRBTreeMap<>();
    private final EnchantmentLevel defaultEnchantmentMaxLevel = new EnchantmentLevel((short) 5);
    private short noVanillaDefault = 5;
    private boolean enabledControl = true;

    public EnchantLevelController(Resource<String> resource) {
        set(resource);
    }

    public void set(Resource<String> resource) {
        for (String res : resource.getNames()) {
            set(new JSONObject(resource.read(res)));
        }
    }

    public void set(JSONObject json) {
        try {
            for (String s : json.keySet()) {
                try {
                    enchantmentsMaxLevel.put(Integer.parseInt(s), new EnchantmentLevel(json.getJSONObject(s)));
                } catch (Exception e) {
                    short def = (short) json.getInt(s);
                    enchantmentsMaxLevel.put(s.hashCode(), new EnchantmentLevel(def));
                }
            }
            enabledControl = json.getBoolean("enable");
        } catch (Exception e) {

        }
    }

    public boolean isEnabledControl() {
        return enabledControl;
    }

    public void setEnabledControl(boolean enabledControl) {
        this.enabledControl = enabledControl;
    }

    public short getNoVanillaDefault() {
        return noVanillaDefault;
    }

    public void setNoVanillaDefault(short noVanillaDefault) {
        this.noVanillaDefault = noVanillaDefault;
    }

    public short getDefaultMaxLevel() {
        return defaultEnchantmentMaxLevel.getDefaultMax();
    }

    public void setNoVanillaDefaultMaxLevel(short max) {
        this.noVanillaDefault = max;
    }

    public void setAll(short max) {
        for (EnchantmentLevel level : enchantmentsMaxLevel.values()) {
            level.setMax(max);
        }
    }

    public void allDefault() {
        for (EnchantmentLevel level : enchantmentsMaxLevel.values()) {
            level.setMax(level.getDefaultMax());
        }
    }

    public EnchantmentLevel getDefaultEnchantmentLevel(String target) {
        try {
            return enchantmentsMaxLevel.get(target.hashCode());
        } catch (Exception e) {
            return defaultEnchantmentMaxLevel;
        }
    }

    public void set(Identifier target, short level) {
        if (target != null) {
            set(target.toString(), level);
        }
    }

    public void set(String target, short level) {
        if (target != null) {
            set(target.hashCode(), level);
        }
    }

    public void set(int hash, short level) {
        enchantmentsMaxLevel.put(hash, enchantmentsMaxLevel.get(hash).setMax(level));
    }

    public EnchantmentLevel get(Identifier target) {
        if (target != null) {
            return get(target.toString());
        }
        return defaultEnchantmentMaxLevel;
    }

    public EnchantmentLevel get(String target) {
        if (target != null) {
            return get(target.hashCode());
        }
        return defaultEnchantmentMaxLevel;
    }

    public EnchantmentLevel get(int hash) {
        return enchantmentsMaxLevel.getOrDefault(hash, new EnchantmentLevel(noVanillaDefault));
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        for (int i : enchantmentsMaxLevel.keySet()) {
            json.put(String.valueOf(i), enchantmentsMaxLevel.get(i).toJSONObject());
        }
        json.put("enable", enabledControl);
        return json;
    }
}
