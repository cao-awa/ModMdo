package com.github.cao.awa.modmdo.mixins.connection;

import com.github.cao.awa.modmdo.develop.text.*;
import com.github.cao.awa.modmdo.storage.*;
import io.netty.channel.*;
import net.minecraft.network.*;
import net.minecraft.text.*;
import org.apache.logging.log4j.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Mixin(ClientConnection.class)
public abstract class ClientConnectionMixin {
    private static final Logger LOGGER = LogManager.getLogger("ModMdoClientConnectionPost");

    private static final Translatable DISCONNECT = Translatable.translatable("Disconnect");

    @Shadow
    private Channel channel;
    @Shadow
    private boolean errored;

    @Shadow public abstract void disconnect(Text disconnectReason);

    @Inject(method = "handleDisconnection", at = @At("HEAD"))
    public void handleDisconnection(CallbackInfo ci) {
        CONNECTIONS.remove((ClientConnection) (Object) this);
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void tick(CallbackInfo ci) {
        if (serverUnderDdosAttack.get()) {
            if (! SharedVariables.CONNECTIONS.contains((ClientConnection) (Object) this)) {
                disconnect(DISCONNECT.text());
                ci.cancel();
            }
        }
    }
}
