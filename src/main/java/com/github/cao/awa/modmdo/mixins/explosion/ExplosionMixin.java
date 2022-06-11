package com.github.cao.awa.modmdo.mixins.explosion;

import com.github.cao.awa.modmdo.event.block.destroy.*;
import com.github.cao.awa.modmdo.storage.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraft.world.explosion.*;
import org.jetbrains.annotations.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {
    @Shadow @Nullable public abstract LivingEntity getCausingEntity();

    @Shadow @Final private double x;

    @Shadow @Final private double y;

    @Shadow @Final private double z;

    @Shadow @Final private float power;

    @Redirect(method = "affectWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
    public boolean affectWorld(World world, BlockPos pos, BlockState state, int flags) {
        if (SharedVariables.extras != null && SharedVariables.extras.isActive(SharedVariables.EXTRA_ID)) {
            BlockState source = world.getBlockState(pos);
            if (!(source.getBlock() instanceof AirBlock)) {
                SharedVariables.event.blockExplosion.previously(new BlockExplosionDestroyEvent(null, source, pos, world, world.getServer()), () -> world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL));
                SharedVariables.event.submit(new BlockExplosionDestroyEvent(new Explosion(world, getCausingEntity(), x, y ,z, power), null, null, null, null));
            }
            return false;
        } else {
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
        }
        return false;
    }
}
