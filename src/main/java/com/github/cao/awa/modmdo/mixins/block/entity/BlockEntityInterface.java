package com.github.cao.awa.modmdo.mixins.block.entity;

import net.minecraft.block.entity.*;
import net.minecraft.nbt.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.*;

@Mixin(BlockEntity.class)
public interface BlockEntityInterface {
    @Invoker("writeIdentifyingData")
    NbtCompound writeIdentifyingData(NbtCompound nbt);
}
