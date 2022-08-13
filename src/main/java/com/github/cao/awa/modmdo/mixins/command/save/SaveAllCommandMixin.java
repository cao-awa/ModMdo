package com.github.cao.awa.modmdo.mixins.command.save;

import com.github.cao.awa.modmdo.backup.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import net.minecraft.server.command.*;
import net.minecraft.server.dedicated.command.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Mixin(SaveAllCommand.class)
public class SaveAllCommandMixin {
    @Inject(method = "saveAll", at = @At("HEAD"), cancellable = true)
    private static void cancelSave(ServerCommandSource source, boolean flush, CallbackInfoReturnable<Integer> cir) {
        if (Archiver.archiving) {
            EntrustExecution.tryTemporary(() -> {
                source.sendError(minecraftTextFormat.format(loginUsers.getUser(source.getPlayer()), "modmdo.archive.building.cancel.save").text());
            });
            cir.setReturnValue(- 1);
        }
    }
}
