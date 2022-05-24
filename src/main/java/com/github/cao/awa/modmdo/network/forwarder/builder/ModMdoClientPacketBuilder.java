package com.github.cao.awa.modmdo.network.forwarder.builder;

import com.github.cao.awa.modmdo.network.forwarder.connection.setting.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.operational.*;
import io.netty.buffer.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.network.*;
import net.minecraft.network.listener.*;
import net.minecraft.network.packet.c2s.play.*;
import org.json.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

public class ModMdoClientPacketBuilder extends PacketBuilder<Packet<ServerPlayPacketListener>> {
    public CustomPayloadC2SPacket buildDisconnect(String reason) {
        return new CustomPayloadC2SPacket(DATA, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(DATA).writeString("disconnect").writeString(reason));
    }

    public CustomPayloadC2SPacket buildChat(String message,String player) {
        JSONObject chat = new JSONObject();
        chat.put("msg", message);
        chat.put("player", player);
        return new CustomPayloadC2SPacket(DATA, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(DATA).writeString("chat").writeString(chat.toString()));
    }

    public CustomPayloadC2SPacket buildSetting(ModMdoConnectionSetting setting) {
        return new CustomPayloadC2SPacket(DATA, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(DATA).writeString("settings").writeString(setting.toJSONObject().toString()));
    }

    public CustomPayloadC2SPacket buildLoginSuccess() {
        return new CustomPayloadC2SPacket(DATA, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(DATA).writeString("login-success").writeString("yes"));
    }

    public CustomPayloadC2SPacket buildPlayerJoin(String name) {
        return new CustomPayloadC2SPacket(DATA, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(DATA).writeString("player-join").writeString(name));
    }

    public CustomPayloadC2SPacket buildPlayerQuit(String name) {
        return new CustomPayloadC2SPacket(DATA, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(DATA).writeString("player-quit").writeString(name));
    }

    public CustomPayloadC2SPacket buildKeepAlive(long lastKeepAlive) {
        return new CustomPayloadC2SPacket(DATA, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(DATA).writeString("keepalive").writeString(String.valueOf(lastKeepAlive)));
    }

    public CustomPayloadC2SPacket buildTraffic(OperationalLong traffic, Object2ObjectRBTreeMap<String, OperationalLong> processed) {
        JSONObject json = new JSONObject();
        json.put("traffic-in", traffic.get());
        JSONObject process = new JSONObject();
        for (String s : processed.keySet()) {
            process.put(s, processed.get(s).get());
        }
        json.put("packets-processed", process);
        return new CustomPayloadC2SPacket(DATA, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(DATA).writeString("traffic").writeString(json.toString()));
    }

    public CustomPayloadC2SPacket buildTrafficResult(OperationalLong traffic, Object2ObjectRBTreeMap<String, OperationalLong> processed) {
        JSONObject json = new JSONObject();
        json.put("traffic-in", traffic.get());
        JSONObject process = new JSONObject();
        for (String s : processed.keySet()) {
            process.put(s, processed.get(s).get());
        }
        json.put("packets-processed", process);
        return new CustomPayloadC2SPacket(DATA, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(DATA).writeString("traffic-result").writeString(json.toString()));
    }
}
