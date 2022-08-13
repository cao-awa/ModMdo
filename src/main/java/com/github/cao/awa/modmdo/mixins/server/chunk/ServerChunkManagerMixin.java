package com.github.cao.awa.modmdo.mixins.server.chunk;

import net.minecraft.server.world.*;
import org.spongepowered.asm.mixin.*;

@Mixin(ServerChunkManager.class)
public abstract class ServerChunkManagerMixin {
//    private final ReentrantLock lock = new ReentrantLock();
//    @Shadow
//    @Final
//    private ServerWorld world;
//    @Shadow
//    @Final
//    private long[] chunkPosCache;
//    @Shadow
//    @Final
//    private ChunkStatus[] chunkStatusCache;
//    @Shadow
//    @Final
//    private Chunk[] chunkCache;
//    @Shadow
//    @Final
//    private Thread serverThread;
//
//    @Inject(method = "getChunk(IILnet/minecraft/world/chunk/ChunkStatus;Z)Lnet/minecraft/world/chunk/Chunk;", at = @At(value = "HEAD"), cancellable = true)
//    public void mutThread(int x, int z, ChunkStatus leastStatus, boolean create, CallbackInfoReturnable<Chunk> cir) {
//        if (Thread.currentThread() != serverThread) {
//            lock.lock();
//            Profiler profiler = this.world.getProfiler();
//            profiler.visit("getChunk");
//            long pos = ChunkPos.toLong(x, z);
//
//            for(int i = 0; i < 4; ++i) {
//                if (pos == this.chunkPosCache[i] && leastStatus == this.chunkStatusCache[i]) {
//                    Chunk chunk = this.chunkCache[i];
//                    if (chunk != null || !create) {
//                        lock.unlock();
//                        cir.setReturnValue(chunk);
//                        return;
//                    }
//                }
//            }
//
//            profiler.visit("getChunkCacheMiss");
//            CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> future = this.getChunkFuture(x, z, leastStatus, create);
//            Chunk chunk;
//            if (future != null) {
//                chunk = future.join().map((chunkx) -> chunkx, (unloaded) -> {
//                    if (create) {
//                        throw Util.throwOrPause(new IllegalStateException("Chunk not there when requested: " + unloaded));
//                    } else {
//                        return null;
//                    }
//                });
//                this.putInCache(pos, chunk, leastStatus);
//                lock.unlock();
//            } else {
//                lock.unlock();
//                throw new NullPointerException();
//            }
//            cir.setReturnValue(chunk);
//        }
//    }
//
//    @Shadow
//    protected abstract CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> getChunkFuture(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create);
//
//    @Shadow
//    protected abstract void putInCache(long pos, Chunk chunk, ChunkStatus status);

}
