package com.github.cao.awa.modmdo.mixins.connection;

import net.minecraft.network.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.TRACKER;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    @Inject(method = "disconnect", at = @At("HEAD"))
    public void disconnect(CallbackInfo ci) {
        TRACKER.submit("Disconnect");
    }
}
