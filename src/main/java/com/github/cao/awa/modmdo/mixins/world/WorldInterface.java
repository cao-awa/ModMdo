package com.github.cao.awa.modmdo.mixins.world;

import net.minecraft.world.*;
import org.spongepowered.asm.mixin.*;

@Mixin(World.class)
public interface WorldInterface extends WorldAccess {
//    @Invoker("tickBlockEntities")
//    void tickBlockEntities();
}
