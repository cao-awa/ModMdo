package com.github.cao.awa.modmdo.mixins.network;

import net.minecraft.network.*;
import net.minecraft.network.packet.c2s.query.*;
import net.minecraft.network.packet.s2c.query.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;
import org.spongepowered.asm.mixin.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.event;

@Mixin(ServerQueryNetworkHandler.class)
public class ServerQueryNetworkHandlerMixin {
    @Shadow private boolean responseSent;

    @Shadow @Final private ClientConnection connection;

    @Shadow @Final private static Text REQUEST_HANDLED;

    @Shadow @Final private MinecraftServer server;

    /**
     * @author 草awa
     * @reason
     */
    @Overwrite
    public void onRequest(QueryRequestC2SPacket packet) {
        if (this.responseSent) {
            this.connection.disconnect(REQUEST_HANDLED);
        } else {
            QueryResponseS2CPacket p = new QueryResponseS2CPacket(this.server.getServerMetadata());
            event.submitQueryRequest(connection, p, server);
            this.responseSent = true;
            this.connection.send(p);
        }
    }
}
