package com.github.zhuaidadaya.modmdo.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.github.zhuaidadaya.modmdo.storage.Variables.*;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public World world;

    @Shadow public abstract BlockPos getBlockPos();

    @Shadow public float fallDistance;

    @Inject(method = "fall", at = @At("HEAD"), cancellable = true)
    private void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition, CallbackInfo ci) {
        if (extras != null && extras.isActive(EXTRA_ID)) {
            if (rejectNoFallCheat & world.getBlockState(getBlockPos().down(1)).toString().equals("Block{minecraft:air}")) {
                this.fallDistance = (float) ((double) this.fallDistance - heightDifference);
                ci.cancel();
            }
        }
    }
}
