package com.github.cao.awa.modmdo.network.forwarder.builder;

import com.github.cao.awa.modmdo.network.forwarder.connection.setting.*;
import io.netty.buffer.*;
import net.minecraft.network.*;
import net.minecraft.network.listener.*;
import net.minecraft.network.packet.c2s.play.*;
import org.json.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

public class ModMdoClientPacketBuilder extends PacketBuilder<Packet<ServerPlayPacketListener>> {
    public CustomPayloadC2SPacket buildDisconnect(String reason) {
        return new CustomPayloadC2SPacket(DATA_CHANNEL, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(DATA_CHANNEL).writeString("disconnect").writeString(reason));
    }

    public CustomPayloadC2SPacket buildChat(String message,String player) {
        JSONObject chat = new JSONObject();
        chat.put("msg", message);
        chat.put("player", player);
        return new CustomPayloadC2SPacket(DATA_CHANNEL, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(DATA_CHANNEL).writeString("chat").writeString(chat.toString()));
    }

    public CustomPayloadC2SPacket buildSetting(ModMdoConnectionSetting setting) {
        return new CustomPayloadC2SPacket(DATA_CHANNEL, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(DATA_CHANNEL).writeString("settings").writeString(setting.toJSONObject().toString()));
    }

    public CustomPayloadC2SPacket buildLoginSuccess() {
        return new CustomPayloadC2SPacket(DATA_CHANNEL, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(DATA_CHANNEL).writeString("login-success").writeString("yes"));
    }

    public CustomPayloadC2SPacket buildPlayerJoin(String name) {
        return new CustomPayloadC2SPacket(DATA_CHANNEL, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(DATA_CHANNEL).writeString("player-join").writeString(name));
    }

    public CustomPayloadC2SPacket buildPlayerQuit(String name) {
        return new CustomPayloadC2SPacket(DATA_CHANNEL, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(DATA_CHANNEL).writeString("player-quit").writeString(name));
    }

    public CustomPayloadC2SPacket buildKeepAlive(long lastKeepAlive) {
        return new CustomPayloadC2SPacket(DATA_CHANNEL, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(DATA_CHANNEL).writeString("keepalive").writeString(String.valueOf(lastKeepAlive)));
    }
}
