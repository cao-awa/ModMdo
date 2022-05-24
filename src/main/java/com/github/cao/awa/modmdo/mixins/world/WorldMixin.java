package com.github.cao.awa.modmdo.mixins.world;

import net.minecraft.block.*;
import net.minecraft.server.*;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.*;
import net.minecraft.world.*;
import net.minecraft.world.border.*;
import org.jetbrains.annotations.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Mixin(World.class)
public abstract class WorldMixin {
    @Shadow @Nullable public abstract MinecraftServer getServer();

    @Shadow public abstract WorldBorder getWorldBorder();

    @Shadow public abstract RegistryKey<World> getRegistryKey();

    @Inject(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z", at = @At("HEAD"))
    public void setBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir) {
       event.submitBlockStateSet(state, pos, flags, maxUpdateDepth, getServer().getWorld(getRegistryKey()) , getServer());
    }
}
