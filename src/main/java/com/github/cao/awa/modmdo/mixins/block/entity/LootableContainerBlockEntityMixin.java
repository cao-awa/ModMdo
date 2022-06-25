package com.github.cao.awa.modmdo.mixins.block.entity;

import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.util.math.*;
import org.spongepowered.asm.mixin.*;

@Mixin(LootableContainerBlockEntity.class)
public abstract class LootableContainerBlockEntityMixin extends LockableContainerBlockEntity{
    protected LootableContainerBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

//    /**
//     * @author 草二号机
//     *
//     */
//    @Redirect(method = "canPlayerUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockEntity(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/entity/BlockEntity;"))
//    public BlockEntity canPlayerUse(World instance, BlockPos pos) {
//        return world == null ? null : world.getWorldChunk(pos).getBlockEntity(pos, WorldChunk.CreationType.IMMEDIATE);
//    }
}
