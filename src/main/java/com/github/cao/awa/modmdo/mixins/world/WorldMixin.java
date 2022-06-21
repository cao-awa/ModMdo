package com.github.cao.awa.modmdo.mixins.world;

import com.github.cao.awa.modmdo.event.block.state.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import net.minecraft.block.*;
import net.minecraft.server.*;
import net.minecraft.util.math.*;
import net.minecraft.util.profiler.*;
import net.minecraft.util.registry.*;
import net.minecraft.world.*;
import net.minecraft.world.chunk.*;
import org.jetbrains.annotations.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Mixin(World.class)
public abstract class WorldMixin implements WorldAccess {
    @Shadow
    @Final
    protected List<BlockEntityTickInvoker> blockEntityTickers;
    @Shadow
    private boolean iteratingTickingBlockEntities;
    @Shadow
    @Final
    private List<BlockEntityTickInvoker> pendingBlockEntityTickers;

    @Inject(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z", at = @At("HEAD"))
    public void setBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir) {
        event.submit(new BlockStateSetEvent(state, pos, flags, maxUpdateDepth, EntrustParser.trying(() -> getServer().getWorld(getRegistryKey())), getServer()));
    }

    @Shadow
    @Nullable
    public abstract MinecraftServer getServer();

    @Shadow
    public abstract RegistryKey<World> getRegistryKey();

    /**
     * @author 草二号机
     * @reason
     */
    @Overwrite
    public void tickBlockEntities() {
        Profiler profiler = this.getProfiler();
        profiler.push("blockEntities");
        this.iteratingTickingBlockEntities = true;
        if (! this.pendingBlockEntityTickers.isEmpty()) {
            this.blockEntityTickers.addAll(this.pendingBlockEntityTickers);
            this.pendingBlockEntityTickers.clear();
        }

        Iterator<BlockEntityTickInvoker> iterator = this.blockEntityTickers.iterator();

        while (iterator.hasNext()) {
            BlockEntityTickInvoker blockEntityTickInvoker = iterator.next();
            if (blockEntityTickInvoker.isRemoved()) {
                iterator.remove();
            } else {
                blockEntityTickInvoker.tick();
            }
        }

        this.iteratingTickingBlockEntities = false;
        profiler.pop();
    }

    @Shadow
    public abstract Profiler getProfiler();
}
