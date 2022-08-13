package com.github.cao.awa.modmdo.mixins.item;

import com.github.cao.awa.modmdo.storage.*;
import net.minecraft.entity.*;
import net.minecraft.world.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    @Shadow
    private int itemAge;

    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }


    /**
     * 修改item的age限制
     *
     * @author 草awa
     * @author 草二号机
     */
    @ModifyConstant(method = "tick", constant = @Constant(intValue = 6000))
    public int tick(int constant) {
        return SharedVariables.itemDespawnAge;
    }
}
