package com.github.zhuaidadaya.modmdo.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.github.zhuaidadaya.modmdo.storage.Variables.itemDespawnAge;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    @Shadow
    private int itemAge;

    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }


    /**
     * @author 草awa
     * @author 草二号机
     *
     * 草二号机取消了重写, 重做了方法
     */


    @Inject(method = "tick",at = @At("RETURN"))
    public void tick(CallbackInfo ci) {
        if (age == -1) {
            age = itemAge;
        }
        age++;
        if (itemAge % 5999 == 0) {
            itemAge = 0;
        }
        if (age > itemDespawnAge) {
            discard();
            age = -1;
        }
    }
}
