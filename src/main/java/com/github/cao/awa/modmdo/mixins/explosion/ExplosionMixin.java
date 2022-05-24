package com.github.cao.awa.modmdo.mixins.explosion;

import com.github.cao.awa.modmdo.event.block.destroy.*;
import com.github.cao.awa.modmdo.storage.*;
import net.minecraft.block.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraft.world.explosion.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(Explosion.class)
public class ExplosionMixin {
    @Redirect(method = "affectWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
    public boolean affectWorld(World world, BlockPos pos, BlockState state, int flags) {
        if (SharedVariables.extras != null && SharedVariables.extras.isActive(SharedVariables.EXTRA_ID)) {
            SharedVariables.event.blockExplosion.previously(new BlockExplosionDestroyEvent(null, state, pos, world, world.getServer()), () -> world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL));
            SharedVariables.event.submitBlockExplosion(null, null, pos, world, null);
            return false;
        }
        return false;
    }
}
