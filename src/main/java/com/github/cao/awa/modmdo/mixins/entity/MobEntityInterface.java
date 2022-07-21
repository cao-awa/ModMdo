package com.github.cao.awa.modmdo.mixins.entity;

import net.minecraft.entity.data.*;
import net.minecraft.entity.mob.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.*;

@Mixin(MobEntity.class)
public interface MobEntityInterface {
    @Accessor
    TrackedData<Byte> getMOB_FLAGS();
}
