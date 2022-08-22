package com.github.cao.awa.modmdo.mixins.enchant;

import com.github.cao.awa.modmdo.storage.*;
import com.google.common.collect.*;
import net.minecraft.enchantment.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.collection.*;
import net.minecraft.util.math.*;
import org.spongepowered.asm.mixin.*;

import java.security.*;
import java.util.*;

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
        if (i <= 0) {
            return list;
        }
        level += 1 + random.nextInt(i / 4 + 1) + random.nextInt(i / 4 + 1);
        float f = (random.nextFloat() + random.nextFloat() - 1.0f) * 0.15f;
        List<EnchantmentLevelEntry> list2 = EnchantmentHelper.getPossibleEntries(level = MathHelper.clamp(Math.round((float) level + (float) level * f), 1, Integer.MAX_VALUE), stack, treasureAllowed);
        if (! list2.isEmpty()) {
            Weighting.getRandom(random, list2).ifPresent(list::add);
            if ((SharedVariables.extras != null && SharedVariables.extras.isActive(SharedVariables.EXTRA_ID)) && SharedVariables.enableSecureEnchant) {
                while (secureRandom.nextInt(50) <= level) {
                    secureRandom = new SecureRandom();
                    if (! list.isEmpty()) {
                        EnchantmentHelper.removeConflicts(list2, Util.getLast(list));
                    }
                    if (list2.isEmpty())
                        break;
                    Weighting.getRandom(random, list2).ifPresent(list::add);
                    level /= 2;
                }
            } else {
                while (random.nextInt(50) <= level) {
                    if (! list.isEmpty()) {
                        EnchantmentHelper.removeConflicts(list2, Util.getLast(list));
                    }
                    if (list2.isEmpty())
                        break;
                    Weighting.getRandom(random, list2).ifPresent(list::add);
                    level /= 2;
                }
            }
        }
        return list;
    }
}
