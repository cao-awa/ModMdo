package com.github.zhuaidadaya.modmdo.mixins;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.github.zhuaidadaya.modmdo.storage.Variables.*;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow
    @Final
    private static Logger LOGGER;
    @Shadow
    private NbtCompound nbt;

    @Shadow
    public abstract Text getName();

    @Inject(method = "setNbt", at = @At("RETURN"))
    public void setNbt(NbtCompound nbt, CallbackInfo ci) {
        filterLevel();
    }

    public void filterLevel() {
        try {
            if (enchantLevelController.isEnabledControl() && nbt != null) {
                NbtList list = nbt.getList("Enchantments", 10);
                NbtList addTo = new NbtList();
                for (NbtElement element : list) {
                    try {
                        NbtCompound enchantment = (NbtCompound) element;
                        String name = enchantment.get("id").asString();
                        String lvl = enchantment.get("lvl").asString();
                        short max = enchantLevelController.get(name.hashCode()).getMax();
                        short real;
                        try {
                            real = Short.parseShort(lvl.replaceAll("\\D+", ""));
                        } catch (Exception e) {
                            LOGGER.warn("level " + lvl + " is too large, removed this element");
                            needSync = true;
                            continue;
                        }
                        if (max == 0) {
                            needSync = true;
                            continue;
                        }
                        if (real > max) {
                            if (clearEnchantIfLevelTooHigh) {
                                addTo = null;
                                needSync = true;
                                LOGGER.warn("a item got invalid enchant nbt, cleared enchant nbt");
                                break;
                            }
                            LOGGER.warn("level of " + name + " out of limit " + max + ": " + real);
                            enchantment.putShort("lvl", enchantLevelController.getDefaultEnchantmentLevel(name).getMax());
                            needSync = true;
                        }
                        addTo.add(enchantment);
                    } catch (Exception e) {
                        if (addTo == null) {
                            break;
                        }
                    }
                }
                nbt.remove("Enchantments");
                if (addTo != null) {
                    nbt.put("Enchantments", addTo);
                }
            }
        } catch (Exception e) {

        }
    }
}