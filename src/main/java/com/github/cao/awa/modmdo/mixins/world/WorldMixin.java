package com.github.cao.awa.modmdo.mixins.world;

import com.github.cao.awa.modmdo.event.block.state.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import net.minecraft.block.*;
import net.minecraft.registry.*;
import net.minecraft.server.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Mixin(World.class)
public abstract class WorldMixin implements WorldAccess {
    @Inject(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z", at = @At("HEAD"))
    public void setBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir) {
        event.submit(new BlockStateSetEvent(
                state,
                pos,
                flags,
                maxUpdateDepth,
                EntrustParser.trying(() -> getServer().getWorld(getRegistryKey())),
                getServer()
        ));
    }

    @Shadow
    @Nullable
    public abstract MinecraftServer getServer();

    @Shadow
    public abstract RegistryKey<World> getRegistryKey();
}
