package com.github.cao.awa.modmdo.mixins.server.chunk;

import net.minecraft.server.world.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.*;

@Mixin(ServerChunkManager.class)
public interface ServerChunkManagerInterface {
    @Invoker("initChunkCaches")
    void initChunkCaches();
}
