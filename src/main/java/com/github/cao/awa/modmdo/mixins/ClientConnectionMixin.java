package com.github.cao.awa.modmdo.mixins;

import net.minecraft.network.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    @Inject(method = "handleDisconnection", at = @At("HEAD"))
    public void handleDisconnection(CallbackInfo ci) {
        tracker.submit("Handle disconnect");
    }
}
