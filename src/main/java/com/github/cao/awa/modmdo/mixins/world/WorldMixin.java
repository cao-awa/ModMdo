package com.github.cao.awa.modmdo.mixins.world;

import com.github.cao.awa.modmdo.event.block.state.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import net.minecraft.block.*;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.*;
import net.minecraft.world.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Mixin(value = World.class)
public abstract class WorldMixin implements WorldAccess {
    @Inject(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z", at = @At("HEAD"))
    public void setBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir) {
        event.submit(new BlockStateSetEvent(state, pos, flags, maxUpdateDepth, EntrustParser.trying(() -> getServer().getWorld(getRegistryKey())), getServer()));
    }

//    @Shadow
//    @Nullable
//    public abstract MinecraftServer getServer();

    @Shadow
    public abstract RegistryKey<World> getRegistryKey();

//    @Shadow public abstract WorldChunk getWorldChunk(BlockPos pos);
//
//    @Shadow public abstract Profiler getProfiler();
//
//    @Shadow private boolean iteratingTickingBlockEntities;
//
//    @Shadow @Final private List<BlockEntityTickInvoker> pendingBlockEntityTickers;
//
//    @Shadow @Final protected List<BlockEntityTickInvoker> blockEntityTickers;
//
//    @Inject(method = "getBlockEntity", at = @At("HEAD"), cancellable = true)
//    public void getBlockEntity(BlockPos pos, CallbackInfoReturnable<BlockEntity> cir) {
//        if (this.isOutOfHeightLimit(pos)) {
//            cir.setReturnValue(null);
//        } else {
//            cir.setReturnValue(getWorldChunk(pos).getBlockEntity(pos, WorldChunk.CreationType.IMMEDIATE));
//        }
//    }
//
//    @Inject(method = "getBlockState", at = @At("HEAD"), cancellable = true)
//    public void getBlockState(BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
//        if (this.isOutOfHeightLimit(pos)) {
//            cir.setReturnValue(Blocks.VOID_AIR.getDefaultState());
//        } else {
//            Chunk chunk = this.getChunk(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()));
//            cir.setReturnValue(chunk.getBlockState(pos));
//        }
//    }

//    /**
//     * @author
//     */
//    @Overwrite
//    public void tickBlockEntities() {
//        Profiler profiler = this.getProfiler();
//        profiler.push("blockEntities");
//        this.iteratingTickingBlockEntities = true;
//        if (!this.pendingBlockEntityTickers.isEmpty()) {
//            this.blockEntityTickers.addAll(this.pendingBlockEntityTickers);
//            this.pendingBlockEntityTickers.clear();
//        }
//
//        if (testingParallel) {
//            ObjectArrayList<BlockEntityTickInvoker> willRemove = new ObjectArrayList<>();
//
//            this.blockEntityTickers.parallelStream().forEach(invoker -> {
//                if (invoker.isRemoved()) {
//                    willRemove.add(invoker);
//                } else {
//                    invoker.tick();
//                }
//            });
//
//            willRemove.parallelStream().forEach(invoker -> blockEntityTickers.remove(invoker));
//        } else {
//            Iterator<BlockEntityTickInvoker> iterator = this.blockEntityTickers.iterator();
//
//            while(iterator.hasNext()) {
//                BlockEntityTickInvoker blockEntityTickInvoker = iterator.next();
//                if (blockEntityTickInvoker.isRemoved()) {
//                    iterator.remove();
//                } else {
//                    blockEntityTickInvoker.tick();
//                }
//            }
//        }
//
//        this.iteratingTickingBlockEntities = false;
//        profiler.pop();
//    }
}
