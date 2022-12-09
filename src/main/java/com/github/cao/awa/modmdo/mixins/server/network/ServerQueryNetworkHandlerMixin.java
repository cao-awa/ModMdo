package com.github.cao.awa.modmdo.mixins.server.network;

import com.github.cao.awa.modmdo.event.server.query.*;
import net.minecraft.network.*;
import net.minecraft.network.packet.c2s.query.*;
import net.minecraft.network.packet.s2c.query.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.event;
import static com.github.cao.awa.modmdo.storage.SharedVariables.TRACKER;

@Mixin(ServerQueryNetworkHandler.class)
public class ServerQueryNetworkHandlerMixin {
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
     * @author 草awa
     * @reason
     */
    @Overwrite
    public void onRequest(QueryRequestC2SPacket packet) {
        TRACKER.submit("Handle request(query)");
        if (this.responseSent) {
            this.connection.disconnect(REQUEST_HANDLED);
        } else {
            QueryResponseS2CPacket p = new QueryResponseS2CPacket(this.server.getServerMetadata());
            event.submit(new ServerQueryEvent(
                    connection,
                    p,
                    server
            ));
            this.responseSent = true;
            this.connection.send(p);
        }
    }

    @Inject(method = "onPing", at = @At("HEAD"))
    public void onPing(QueryPingC2SPacket packet, CallbackInfo ci) {
        TRACKER.submit("Handle ping(query)");
    }
}
