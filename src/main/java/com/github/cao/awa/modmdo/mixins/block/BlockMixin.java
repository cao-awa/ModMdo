package com.github.cao.awa.modmdo.mixins.block;

import com.github.cao.awa.modmdo.event.block.destroy.*;
import com.github.cao.awa.modmdo.event.block.place.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(method = "onBreak", at = @At("HEAD"))
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfo ci) {
        EntrustExecution.tryTemporary(() -> {
            SharedVariables.event.submit(new BlockBreakEvent(
                    player,
                    state,
                    pos,
                    world,
                    player.getServer()
            ));
        });
    }

    @Inject(method = "onPlaced", at = @At("HEAD"))
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack, CallbackInfo ci) {
        EntrustExecution.tryTemporary(() -> {
            SharedVariables.event.submit(new BlockPlaceEvent(
                    placer,
                    state,
                    pos,
                    world,
                    itemStack,
                    placer.getServer()
            ));
        });
    }
}
