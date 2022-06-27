package com.github.cao.awa.modmdo.network.forwarder.builder;

import com.github.cao.awa.modmdo.network.forwarder.connection.setting.*;
import com.github.cao.awa.modmdo.storage.*;
import io.netty.buffer.*;
import net.minecraft.network.*;
import net.minecraft.network.listener.*;
import net.minecraft.network.packet.s2c.play.*;
import org.json.*;

public class ModMdoServerPacketBuilder extends PacketBuilder<Packet<ClientPlayPacketListener>> {
    public CustomPayloadS2CPacket buildDisconnect(String reason) {
        return new CustomPayloadS2CPacket(SharedVariables.DATA_CHANNEL, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(SharedVariables.DATA_CHANNEL).writeString("disconnect").writeString(reason));
    }

    public CustomPayloadS2CPacket buildChat(String message, String player) {
        JSONObject chat = new JSONObject();
        chat.put("msg", message);
        chat.put("player", player);
        return new CustomPayloadS2CPacket(SharedVariables.DATA_CHANNEL, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(SharedVariables.DATA_CHANNEL).writeString("chat").writeString(chat.toString()));
    }

    public CustomPayloadS2CPacket buildSetting(ModMdoConnectionSetting setting) {
        return new CustomPayloadS2CPacket(SharedVariables.DATA_CHANNEL, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(SharedVariables.DATA_CHANNEL).writeString("settings").writeString(setting.toJSONObject().toString()));
    }

    public CustomPayloadS2CPacket buildLoginSuccess() {
        return new CustomPayloadS2CPacket(SharedVariables.DATA_CHANNEL, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(SharedVariables.DATA_CHANNEL).writeString("login-success").writeString("yes"));
    }

    public CustomPayloadS2CPacket buildPlayerJoin(String name) {
        return new CustomPayloadS2CPacket(SharedVariables.DATA_CHANNEL, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(SharedVariables.DATA_CHANNEL).writeString("player-join").writeString(name));
    }

    public CustomPayloadS2CPacket buildPlayerQuit(String name) {
        return new CustomPayloadS2CPacket(SharedVariables.DATA_CHANNEL, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(SharedVariables.DATA_CHANNEL).writeString("player-quit").writeString(name));
    }

    public CustomPayloadS2CPacket buildKeepAlive(long lastKeepAlive) {
        return new CustomPayloadS2CPacket(SharedVariables.DATA_CHANNEL, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(SharedVariables.DATA_CHANNEL).writeString("keepalive").writeString(String.valueOf(lastKeepAlive)));
    }
}
