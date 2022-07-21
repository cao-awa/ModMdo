package com.github.cao.awa.modmdo.mixins.server.chunk.pos;

import net.minecraft.util.math.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(ChunkPos.class)
public class ChunkPosMixin {
    @Inject(method = "toLong(II)J", at = @At("HEAD"), cancellable = true)
    private static void toLong(int chunkX, int chunkZ, CallbackInfoReturnable<Long> cir) {
        cir.setReturnValue(chunkX & 0xFFFFFFFFL | (long) chunkZ << 0x20);
    }
}
