package com.github.zhuaidadaya.modmdo.simple.info;

import com.github.zhuaidadaya.modmdo.simple.vec.RXY;
import com.github.zhuaidadaya.modmdo.simple.vec.XYZ;
import com.github.zhuaidadaya.modmdo.utils.dimension.DimensionUtil;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.json.JSONObject;

import java.util.Collection;

import static com.github.zhuaidadaya.modmdo.storage.SharedVariables.fractionDigits2;

public class PlayerInfo {
    private final XYZ xyz;
    private final RXY rxy;
    private final String dimension;
    private final PlayerInventory inventory;
    private final float health;
    private final float hungry;
    private final Collection<StatusEffectInstance> effects;

    public PlayerInfo(ServerPlayerEntity player) {
        xyz = new XYZ(player.getPos());
        rxy = new RXY(player.getRotationClient());
        dimension = DimensionUtil.getDimension(player);
        inventory = player.getInventory();
        health = player.getHealth();
        hungry = player.getHungerManager().getExhaustion();
        effects = player.getStatusEffects();
    }

    public XYZ getXyz() {
        return xyz;
    }

    public PlayerInventory getInventory() {
        return inventory;
    }

    public float getHealth() {
        return health;
    }

    public float getHungry() {
        return hungry;
    }

    public Collection<StatusEffectInstance> getEffects() {
        return effects;
    }

    public RXY getRxy() {
        return rxy;
    }

    public String getDimension() {
        return dimension;
    }

    public String toString() {
        return toJSONObject().toString();
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        json.put("xyz", xyz.toJSONObject());
        json.put("rxy", rxy.toJSONObject());
        json.put("dimension", dimension);
        JSONObject inv = new JSONObject();
        for (int i = 0; i < inventory.size(); ++ i) {
            ItemStack stack = inventory.getStack(i);
            int count = stack.getCount();
            if (count == 0) {
                continue;
            }
            JSONObject item = new JSONObject();
            item.put("count", count);
            item.put("maxCount", stack.getMaxCount());
            item.put("damage", stack.getDamage());
            inv.put(String.valueOf(i), item);
        }
        json.put("inventory", inv);
        JSONObject efs = new JSONObject();
        for (StatusEffectInstance effect : effects) {
            JSONObject ef = new JSONObject();
            ef.put("duration", effect.getDuration());
            ef.put("amplifier", effect.getAmplifier());
            efs.put(effect.getTranslationKey(), ef);
        }
        json.put("effects", efs);
        json.put("health", fractionDigits2.format(health));
        json.put("hungry", fractionDigits2.format(hungry));
        return json;
    }
}
