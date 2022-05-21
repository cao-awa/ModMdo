package com.github.zhuaidadaya.modmdo.mixins.block;

import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraft.world.explosion.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import static com.github.zhuaidadaya.modmdo.storage.Variables.*;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(method = "onBreak", at = @At("HEAD"))
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfo ci) {
        event.submitBlockDestroy(player, state, pos, world, player.getServer());
    }

    @Inject(method = "onPlaced", at = @At("HEAD"))
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack, CallbackInfo ci) {
        event.submitBlockPlace(placer, state, pos, world, itemStack, placer.getServer());
    }
}
