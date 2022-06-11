package com.github.cao.awa.modmdo.mixins.command;

import com.github.cao.awa.modmdo.event.command.*;
import net.minecraft.server.command.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.event;

@Mixin(CommandManager.class)
public class CommandManagerMixin {
    @Inject(method = "execute", at = @At("HEAD"))
    public void execute(ServerCommandSource commandSource, String command, CallbackInfoReturnable<Integer> cir) {
        event.submit(new CommandExecuteEvent(commandSource, command, commandSource.getServer()));
    }
}
