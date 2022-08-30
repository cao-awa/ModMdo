package com.github.cao.awa.modmdo.mixins.server.chunk.pos;

import net.minecraft.util.math.*;
import org.jetbrains.annotations.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

@Mixin(ChunkPos.class)
public class ChunkPosMixin {
    @Inject(method = "toLong(II)J", at = @At("HEAD"), cancellable = true)
    private static void toLong(int chunkX, int chunkZ, CallbackInfoReturnable<Long> cir) {
        cir.setReturnValue(chunkX & 0xFFFFFFFFL | (long) chunkZ << 0x20);
    }

    @Inject(method = "stream(Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/util/math/ChunkPos;)Ljava/util/stream/Stream;", at = @At("HEAD"), cancellable = true)
    private static void stream(ChunkPos pos1, ChunkPos pos2, CallbackInfoReturnable<Stream<ChunkPos>> cir) {
        final int naX = pos1.x < pos2.x ? 1 : -1;
        final int naZ = pos1.z < pos2.z ? 1 : -1;
        cir.setReturnValue(StreamSupport.stream(new Spliterators.AbstractSpliterator<>((long) (Math.abs(pos1.x - pos2.x) + 1) * (Math.abs(pos1.z - pos2.z) + 1), Spliterator.SIZED) {
            @Nullable
            private ChunkPos position;

            public boolean tryAdvance(Consumer<? super ChunkPos> consumer) {
                if (this.position == null) {
                    this.position = pos1;
                } else {
                    if (this.position.x == pos2.x) {
                        if (this.position.z == pos2.z) {
                            return false;
                        }

                        this.position = new ChunkPos(pos1.x, this.position.z + naZ);
                    } else {
                        this.position = new ChunkPos(this.position.x + naX, this.position.z);
                    }
                }

                consumer.accept(this.position);
                return true;
            }
        }, false));
    }
}
