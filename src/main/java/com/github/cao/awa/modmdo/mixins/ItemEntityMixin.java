package com.github.cao.awa.modmdo.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    @Shadow
    private int itemAge;

    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }


    /**
     * 草二号机取消了重写, 重做了方法
     *
     * @author 草awa
     * @author 草二号机
     *
     */
    @Inject(method = "tick",at = @At("RETURN"))
    public void tick(CallbackInfo ci) {
        if (extras != null && extras.isActive(EXTRA_ID)) {
            if (age == - 1) {
                age = itemAge;
            }
            age++;
            if (itemAge % 5999 == 0) {
                itemAge = 0;
            }
            if (age > itemDespawnAge) {
                discard();
                age = - 1;
            }
        }
    }
}
