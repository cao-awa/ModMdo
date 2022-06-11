package com.github.cao.awa.modmdo.mixins;

import com.github.cao.awa.modmdo.develop.text.*;
import com.github.cao.awa.modmdo.network.forwarder.handler.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.utils.text.*;
import net.minecraft.*;
import net.minecraft.network.*;
import net.minecraft.network.packet.c2s.handshake.*;
import net.minecraft.network.packet.s2c.login.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;
import org.spongepowered.asm.mixin.*;

import java.net.*;

@Mixin(ServerHandshakeNetworkHandler.class)
public class ServerHandshakeNetworkHandlerMixin {
    @Shadow @Final private ClientConnection connection;

    @Shadow @Final private MinecraftServer server;

    @Shadow @Final private static Text IGNORING_STATUS_REQUEST_MESSAGE;

    /**
     * @author 草awa
     */
    @Overwrite
    public void onHandshake(HandshakeC2SPacket packet) {
        switch(packet.getIntendedState()) {
            case LOGIN:
                this.connection.setState(NetworkState.LOGIN);
                if (packet.getProtocolVersion() != SharedConstants.getGameVersion().getProtocolVersion()) {
                    Translatable text;
                    if (packet.getProtocolVersion() < 754) {
                        text = TextUtil.translatable("multiplayer.disconnect.outdated_client", SharedConstants.getGameVersion().getName());
                    } else {
                        text = TextUtil.translatable("multiplayer.disconnect.incompatible", SharedConstants.getGameVersion().getName());
                    }

                    this.connection.send(new LoginDisconnectS2CPacket(text.text()));
                    this.connection.disconnect(text.text());
                } else {
                    this.connection.setPacketListener(new ServerLoginNetworkHandler(this.server, this.connection));
                }
                break;
            case STATUS:
                if (this.server.acceptsStatusQuery()) {
                    this.connection.setState(NetworkState.STATUS);
                    this.connection.setPacketListener(new ServerQueryNetworkHandler(this.server, this.connection));
                } else {
                    this.connection.disconnect(IGNORING_STATUS_REQUEST_MESSAGE);
                }
                break;
            case PLAY:
                if (SharedVariables.isActive()) {
                    this.connection.setState(NetworkState.PLAY);
                    new ModMdoServerDataHandler(this.server, (InetSocketAddress) connection.getAddress(), this.connection);
                }
                break;
            default:
                throw new UnsupportedOperationException("Invalid intention " + packet.getIntendedState());
        }
    }
}
