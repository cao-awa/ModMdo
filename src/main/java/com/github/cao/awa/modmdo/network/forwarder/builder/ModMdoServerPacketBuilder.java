package com.github.cao.awa.modmdo.network.forwarder.builder;

import com.github.cao.awa.modmdo.network.forwarder.connection.setting.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.operational.*;
import io.netty.buffer.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.network.*;
import net.minecraft.network.listener.*;
import net.minecraft.network.packet.s2c.play.*;
import org.json.*;

public class ModMdoServerPacketBuilder extends PacketBuilder<Packet<ClientPlayPacketListener>> {
    public CustomPayloadS2CPacket buildDisconnect(String reason) {
        return new CustomPayloadS2CPacket(SharedVariables.DATA, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(SharedVariables.DATA).writeString("disconnect").writeString(reason));
    }

    public CustomPayloadS2CPacket buildChat(String message, String player) {
        JSONObject chat = new JSONObject();
        chat.put("msg", message);
        chat.put("player", player);
        return new CustomPayloadS2CPacket(SharedVariables.DATA, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(SharedVariables.DATA).writeString("chat").writeString(chat.toString()));
    }

    public CustomPayloadS2CPacket buildSetting(ModMdoConnectionSetting setting) {
        return new CustomPayloadS2CPacket(SharedVariables.DATA, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(SharedVariables.DATA).writeString("settings").writeString(setting.toJSONObject().toString()));
    }

    public CustomPayloadS2CPacket buildLoginSuccess() {
        return new CustomPayloadS2CPacket(SharedVariables.DATA, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(SharedVariables.DATA).writeString("login-success").writeString("yes"));
    }

    public CustomPayloadS2CPacket buildPlayerJoin(String name) {
        return new CustomPayloadS2CPacket(SharedVariables.DATA, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(SharedVariables.DATA).writeString("player-join").writeString(name));
    }

    public CustomPayloadS2CPacket buildPlayerQuit(String name) {
        return new CustomPayloadS2CPacket(SharedVariables.DATA, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(SharedVariables.DATA).writeString("player-quit").writeString(name));
    }

    public CustomPayloadS2CPacket buildKeepAlive(long lastKeepAlive) {
        return new CustomPayloadS2CPacket(SharedVariables.DATA, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(SharedVariables.DATA).writeString("keepalive").writeString(String.valueOf(lastKeepAlive)));
    }

    public CustomPayloadS2CPacket buildTraffic(OperationalLong traffic, Object2ObjectRBTreeMap<String, OperationalLong> processed) {
        JSONObject json = new JSONObject();
        json.put("traffic-in", traffic.get());
        JSONObject process = new JSONObject();
        for (String s : processed.keySet()) {
            process.put(s, processed.get(s).get());
        }
        json.put("packets-processed", process);
        return new CustomPayloadS2CPacket(SharedVariables.DATA, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(SharedVariables.DATA).writeString("traffic").writeString(json.toString()));
    }

    public CustomPayloadS2CPacket buildTrafficResult(OperationalLong traffic, Object2ObjectRBTreeMap<String, OperationalLong> processed) {
        JSONObject json = new JSONObject();
        json.put("traffic-in", traffic.get());
        JSONObject process = new JSONObject();
        for (String s : processed.keySet()) {
            process.put(s, processed.get(s).get());
        }
        json.put("packets-processed", process);
        return new CustomPayloadS2CPacket(SharedVariables.DATA, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(SharedVariables.DATA).writeString("traffic-result").writeString(json.toString()));
    }
}
