package com.github.cao.awa.modmdo.mixins;

import com.github.cao.awa.modmdo.storage.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.text.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow
    private NbtCompound nbt;

    @Shadow
    public abstract Text getName();

    @Inject(method = "setNbt", at = @At("RETURN"))
    public void setNbt(NbtCompound nbt, CallbackInfo ci) {
        filterLevel();
    }

    public void filterLevel() {
        EntrustExecution.tryTemporary(() -> {
            if (SharedVariables.enchantLevelController.isEnabledControl() && nbt != null) {
                NbtList list = nbt.getList("Enchantments", 10);
                if (list.size() > 0) {
                    NbtList addTo = new NbtList();
                    for (NbtElement element : list) {
                        try {
                            NbtCompound enchantment = (NbtCompound) element;
                            String name = enchantment.get("id").asString();
                            String lvl = enchantment.get("lvl").asString();
                            short max = SharedVariables.enchantLevelController.get(name.hashCode()).getMax();
                            short real;
                            try {
                                real = Short.parseShort(lvl.replaceAll("\\D+", ""));
                            } catch (Exception e) {
                                LOGGER.warn("level " + lvl + " is too large, removed this element");
                                continue;
                            }
                            if (max == 0) {
                                continue;
                            }
                            if (real > max) {
                                if (SharedVariables.clearEnchantIfLevelTooHigh) {
                                    addTo = null;
                                    LOGGER.warn("a item got invalid enchant nbt, cleared enchant nbt");
                                    break;
                                }
                                LOGGER.warn("level of " + name + " out of limit " + max + ": " + real);
                                enchantment.putShort("lvl", SharedVariables.enchantLevelController.getDefaultEnchantmentLevel(name).getMax());
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
            }
        });
    }
}
