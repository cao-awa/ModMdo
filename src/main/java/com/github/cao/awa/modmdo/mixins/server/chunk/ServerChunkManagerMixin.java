package com.github.cao.awa.modmdo.mixins.server.chunk;

import com.mojang.datafixers.util.*;
import net.minecraft.server.world.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.profiler.*;
import net.minecraft.world.chunk.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

@Mixin(ServerChunkManager.class)
public abstract class ServerChunkManagerMixin {
    private final ReentrantLock lock = new ReentrantLock();
    @Shadow
    @Final
    private ServerWorld world;
    @Shadow
    @Final
    private long[] chunkPosCache;
    @Shadow
    @Final
    private ChunkStatus[] chunkStatusCache;
    @Shadow
    @Final
    private Chunk[] chunkCache;
    @Shadow
    @Final
    private Thread serverThread;

    @Inject(method = "getChunk(IILnet/minecraft/world/chunk/ChunkStatus;Z)Lnet/minecraft/world/chunk/Chunk;", at = @At(value = "HEAD"), cancellable = true)
    public void mutThread(int x, int z, ChunkStatus leastStatus, boolean create, CallbackInfoReturnable<Chunk> cir) {
        lock.lock();
        if (Thread.currentThread() != serverThread) {
            Profiler profiler = this.world.getProfiler();
            profiler.visit("getChunk");
            long l = ChunkPos.toLong(x, z);

            Chunk chunk;
            for (int i = 0; i < 4; ++ i) {
                if (l == this.chunkPosCache[i] && leastStatus == this.chunkStatusCache[i]) {
                    chunk = this.chunkCache[i];
                    if (chunk != null || ! create) {
                        lock.unlock();
                        cir.setReturnValue(chunk);
                        return;
                    }
                }
            }

            profiler.visit("getChunkCacheMiss");
            CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = this.getChunkFuture(x, z, leastStatus, create);
            Objects.requireNonNull(completableFuture);
            chunk = completableFuture.join().map((chunkx) -> chunkx, (unloaded) -> {
                if (create) {
                    throw Util.throwOrPause(new IllegalStateException("Chunk not there when requested: " + unloaded));
                } else {
                    return null;
                }
            });
            this.putInCache(l, chunk, leastStatus);
            lock.unlock();
            cir.setReturnValue(chunk);
        }
    }

    @Inject(method = "getChunk(IILnet/minecraft/world/chunk/ChunkStatus;Z)Lnet/minecraft/world/chunk/Chunk;", at = @At("RETURN"))
    public void getChunk(int x, int z, ChunkStatus leastStatus, boolean create, CallbackInfoReturnable<Chunk> cir) {
        lock.unlock();
    }

    @Shadow
    protected abstract CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> getChunkFuture(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create);

    @Shadow
    protected abstract void putInCache(long pos, Chunk chunk, ChunkStatus status);
}
