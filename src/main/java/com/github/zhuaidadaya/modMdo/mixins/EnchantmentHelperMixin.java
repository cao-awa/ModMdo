package com.github.zhuaidadaya.modMdo.mixins;

import com.google.common.collect.Lists;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import net.minecraft.util.collection.Weighting;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.github.zhuaidadaya.modMdo.storage.Variables.enableSecureEnchant;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    /**
     * @author 草awa
     * @reason
     */
    @Overwrite
    public static List<EnchantmentLevelEntry> generateEnchantments(Random random, ItemStack stack, int level, boolean treasureAllowed) {
        SecureRandom secureRandom = new SecureRandom();
        ArrayList<EnchantmentLevelEntry> list = Lists.newArrayList();
        Item item = stack.getItem();
        int i = item.getEnchantability();
        if(i <= 0) {
            return list;
        }
        level += 1 + random.nextInt(i / 4 + 1) + random.nextInt(i / 4 + 1);
        float f = (random.nextFloat() + random.nextFloat() - 1.0f) * 0.15f;
        List<EnchantmentLevelEntry> list2 = EnchantmentHelper.getPossibleEntries(level = MathHelper.clamp(Math.round((float) level + (float) level * f), 1, Integer.MAX_VALUE), stack, treasureAllowed);
        if(! list2.isEmpty()) {
            Weighting.getRandom(random, list2).ifPresent(list :: add);
            if(enableSecureEnchant) {
                while(secureRandom.nextInt(50) <= level) {
                    secureRandom = new SecureRandom();
                    if(! list.isEmpty()) {
                        EnchantmentHelper.removeConflicts(list2, Util.getLast(list));
                    }
                    if(list2.isEmpty())
                        break;
                    Weighting.getRandom(random, list2).ifPresent(list :: add);
                    level /= 2;
                }
            } else {
                while(random.nextInt(50) <= level) {
                    if(! list.isEmpty()) {
                        EnchantmentHelper.removeConflicts(list2, Util.getLast(list));
                    }
                    if(list2.isEmpty())
                        break;
                    Weighting.getRandom(random, list2).ifPresent(list :: add);
                    level /= 2;
                }
            }
        }
        return list;
    }
}
