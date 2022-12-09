package com.github.cao.awa.modmdo.mixins.server.network;

import com.github.cao.awa.modmdo.event.server.query.*;
import net.minecraft.network.*;
import net.minecraft.network.packet.c2s.query.*;
import net.minecraft.network.packet.s2c.query.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;
import org.apache.logging.log4j.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Mixin(ServerQueryNetworkHandler.class)
public class ServerQueryNetworkHandlerMixin {
    private static final Logger LOGGER = LogManager.getLogger("ModMdoServerQueryHandler");

    @Shadow
    @Final
    private static Text REQUEST_HANDLED;
    @Shadow
    private boolean responseSent;
    @Shadow
    @Final
    private ClientConnection connection;
    @Shadow
    @Final
    private MinecraftServer server;

    /**
     * @author cao_awa
     * @author 草二号机
     * @reason
     */
    @Inject(method = "onRequest", at = @At("HEAD"), cancellable = true)
    public void onRequest(QueryRequestC2SPacket packet, CallbackInfo ci) {
        if (this.responseSent) {
            this.connection.disconnect(REQUEST_HANDLED);
        } else {
            QueryResponseS2CPacket p = new QueryResponseS2CPacket(this.server.getServerMetadata());
            event.submit(new ServerQueryEvent(
                    this.connection,
                    p,
                    this.server
            ));
            this.responseSent = true;
            this.connection.send(p);
        }
        ci.cancel();
    }
}
