package com.github.zhuaidadaya.modMdo.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import static com.github.zhuaidadaya.modMdo.storage.Variables.itemDespawnAge;

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
     * @reason
     */
    @Overwrite
    public void tick() {
        if(getStack().isEmpty()) {
            discard();
        } else {
            super.tick();
            if(pickupDelay > 0 && pickupDelay != 32767) {
                -- pickupDelay;
            }
            float f = getStandingEyeHeight() - 0.11111111F;
            if(isTouchingWater() && getFluidHeight(FluidTags.WATER) > (double) f) {
                applyWaterBuoyancy();
            } else if(isInLava() && getFluidHeight(FluidTags.LAVA) > (double) f) {
                applyLavaBuoyancy();
            } else if(! hasNoGravity()) {
                setVelocity(getVelocity().add(0.0D, - 0.04D, 0.0D));
            }

            if(world.isClient) {
                noClip = false;
            } else {
                noClip = ! world.isSpaceEmpty(this, getBoundingBox().contract(1.0E-7D));
                if(noClip) {
                    pushOutOfBlocks(getX(), (getBoundingBox().minY + getBoundingBox().maxY) / 2, getZ());
                }
            }

            if(! onGround || getVelocity().horizontalLengthSquared() > 9.999999747378752E-6D || (age + getId()) % 4 == 0) {
                move(MovementType.SELF, getVelocity());
                float g = 0.98F;
                if(onGround) {
                    g = world.getBlockState(new BlockPos(getX(), getY() - 1.0D, getZ())).getBlock().getSlipperiness() * 0.98F;

                    setVelocity(getVelocity().multiply(g, 0.98D, g));

                    Vec3d vec3d2 = getVelocity();
                    if(vec3d2.y < 0.0D) {
                        setVelocity(vec3d2.multiply(1.0D, - 0.5D, 1.0D));
                    }
                } else {
                    setVelocity(getVelocity().multiply(g, 0.98D, g));
                }
            }

            boolean g = MathHelper.floor(prevX) != MathHelper.floor(getX()) || MathHelper.floor(prevY) != MathHelper.floor(getY()) || MathHelper.floor(prevZ) != MathHelper.floor(getZ());
            int vec3d2 = g ? 2 : 40;
            if(age % vec3d2 == 0 && ! world.isClient && canMerge()) {
                tryMerge();
            }

            if(itemAge > -1)
                ++ itemAge;

            velocityDirty |= updateWaterState();
            if(! world.isClient) {
                velocityDirty = getVelocity().subtract(getVelocity()).lengthSquared() > 0.01D;
                if(itemDespawnAge > -1) {
                    if(itemAge > itemDespawnAge) {
                        discard();
                    }
                }
            }
        }
    }
}
