package com.github.cao.awa.modmdo.mixins.command.block;

import com.github.cao.awa.modmdo.event.command.block.*;
import net.minecraft.block.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.event;

@Mixin(CommandBlock.class)
public class CommandBlockMixin {
    @Inject(method = "execute", at = @At("HEAD"))
    public void execute(BlockState state, World world, BlockPos pos, CommandBlockExecutor executor, boolean hasCommand, CallbackInfo ci) {
        event.submit(new CommandBlockExecuteEvent(pos, state, executor, world, world.getServer()));
    }
}
