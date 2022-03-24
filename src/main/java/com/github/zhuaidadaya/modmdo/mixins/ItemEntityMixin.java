package com.github.zhuaidadaya.modmdo.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.github.zhuaidadaya.modmdo.storage.Variables.itemDespawnAge;

/**
 * TAG:DRT|SKP|VSD
 * 这个tag用于注明这是有版本差异的
 * 存在这个tag时不会直接从其他正在开发的部分复制
 * 而是手动替换
 * TAG:
 * DRT(Don't Replace It)
 * SKP(Skip)
 * VSD(Version Difference)
 * <p>
 * 手动替换检测: 1.17.x
 */
@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    @Shadow
    private int pickupDelay;
    @Shadow
    private int itemAge;

    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    public abstract ItemStack getStack();

    @Shadow
    protected abstract void applyWaterBuoyancy();

    @Shadow
    protected abstract void applyLavaBuoyancy();

    @Shadow
    protected abstract void tryMerge();

    @Shadow
    protected abstract boolean canMerge();

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
