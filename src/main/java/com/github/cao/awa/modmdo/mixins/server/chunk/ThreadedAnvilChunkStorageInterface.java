package com.github.cao.awa.modmdo.mixins.server.chunk;

import it.unimi.dsi.fastutil.longs.*;
import net.minecraft.server.world.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.*;

import java.util.function.*;

@Mixin(ThreadedAnvilChunkStorage.class)
public interface ThreadedAnvilChunkStorageInterface {
    @Invoker("unloadChunks")
    void unloadChunks(BooleanSupplier shouldKeepTick);

    @Accessor
    Long2ObjectLinkedOpenHashMap<ChunkHolder> getCurrentChunkHolders();

    @Accessor
    Long2ObjectLinkedOpenHashMap<ChunkHolder> getChunkHolders();

    @Accessor
    Long2ObjectLinkedOpenHashMap<ChunkHolder> getChunksToUnload();
}
