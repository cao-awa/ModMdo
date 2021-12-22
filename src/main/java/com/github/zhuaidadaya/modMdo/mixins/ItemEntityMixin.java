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
 * 手动替换检测: 1.18.x
 */
@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow public abstract ItemStack getStack();

    @Shadow private int pickupDelay;

    @Shadow protected abstract void applyWaterBuoyancy();

    @Shadow protected abstract void applyLavaBuoyancy();

    @Shadow protected abstract void tryMerge();

    @Shadow protected abstract boolean canMerge();

    @Shadow private int itemAge;

    /**
     * @author 草awa
     * @reason
     */
    @Overwrite
    public void tick() {
        if (this.getStack().isEmpty()) {
            this.discard();
        } else {
            super.tick();
            if (this.pickupDelay > 0 && this.pickupDelay != 32767) {
                --this.pickupDelay;
            }

            this.prevX = this.getX();
            this.prevY = this.getY();
            this.prevZ = this.getZ();
            Vec3d vec3d = this.getVelocity();
            float f = this.getStandingEyeHeight() - 0.11111111F;
            if (this.isTouchingWater() && this.getFluidHeight(FluidTags.WATER) > (double)f) {
                this.applyWaterBuoyancy();
            } else if (this.isInLava() && this.getFluidHeight(FluidTags.LAVA) > (double)f) {
                this.applyLavaBuoyancy();
            } else if (!this.hasNoGravity()) {
                this.setVelocity(this.getVelocity().add(0.0D, -0.04D, 0.0D));
            }

            if (this.world.isClient) {
                this.noClip = false;
            } else {
                this.noClip = !this.world.isSpaceEmpty(this, this.getBoundingBox().contract(1.0E-7));
                if (this.noClip) {
                    this.pushOutOfBlocks(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2, this.getZ());
                }
            }

            if (!this.onGround || this.getVelocity().horizontalLengthSquared() > 9.999999747378752E-6D || (this.age + this.getId()) % 4 == 0) {
                this.move(MovementType.SELF, this.getVelocity());
                float g = 0.98F;
                if (this.onGround) {
                    g = this.world.getBlockState(new BlockPos(this.getX(), this.getY() - 1.0D, this.getZ())).getBlock().getSlipperiness() * 0.98F;
                }

                this.setVelocity(this.getVelocity().multiply(g, 0.98D, g));
                if (this.onGround) {
                    Vec3d vec3d2 = this.getVelocity();
                    if (vec3d2.y < 0.0D) {
                        this.setVelocity(vec3d2.multiply(1.0D, -0.5D, 1.0D));
                    }
                }
            }

            boolean g = MathHelper.floor(this.prevX) != MathHelper.floor(this.getX()) ||
                        MathHelper.floor(this.prevY) != MathHelper.floor(this.getY()) ||
                        MathHelper.floor(this.prevZ) != MathHelper.floor(this.getZ());
            int vec3d2 = g ? 2 : 40;
            if (this.age % vec3d2 == 0 && !this.world.isClient && this.canMerge()) {
                this.tryMerge();
            }

            if (this.itemAge != -32768) {
                ++this.itemAge;
            }

            this.velocityDirty |= this.updateWaterState();
            if (!this.world.isClient) {
                double d = this.getVelocity().subtract(vec3d).lengthSquared();
                if (d > 0.01D) {
                    this.velocityDirty = true;
                }
            }

            if (!this.world.isClient && this.itemAge >= itemDespawnAge) {
                this.discard();
            }
        }
    }
}
