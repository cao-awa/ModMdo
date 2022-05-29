package com.github.cao.awa.modmdo.mixins;

import com.github.cao.awa.modmdo.storage.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public World world;

    @Shadow public abstract BlockPos getBlockPos();

    @Shadow public float fallDistance;

    @Inject(method = "fall", at = @At("HEAD"), cancellable = true)
    private void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition, CallbackInfo ci) {
        if (SharedVariables.isActive()) {
            if (rejectNoFallCheat & world.getBlockState(getBlockPos().down(1)).toString().equals("Block{minecraft:air}")) {
                this.fallDistance = (float) ((double) this.fallDistance - heightDifference);
                ci.cancel();
            }
        }
    }
}
