package com.github.zhuaidadaya.modmdo.jump.server;

import com.github.zhuaidadaya.modmdo.login.token.ServerEncryptionToken;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import org.json.JSONObject;

import java.util.Collection;
import java.util.LinkedHashMap;

import static com.github.zhuaidadaya.modmdo.storage.Variables.modMdoServerChannel;

public class ServerUtil {
    private final LinkedHashMap<String, ServerInformation> servers = new LinkedHashMap<>();

    public ServerUtil() {

    }

    public ServerUtil(JSONObject json) {
        for(String name : json.keySet()) {
            servers.put(name, new ServerInformation(json.getJSONObject(name)));
        }
    }

    public void add(String host, int port, String name) {
        servers.put(name, new ServerInformation(host, port, name));
    }

    public void add(String host, int port, String name, ServerEncryptionToken token) {
        servers.put(name, new ServerInformation(host, port, name, token));
    }

    public void set(ServerInformation server) {
        servers.put(server.getName(),server);
    }

    public void set(String name, ServerInformation server) {
        servers.put(name, server);
    }

    public ServerInformation getServer(String name) {
        return servers.get(name);
    }

    public void jump(String name, MinecraftClient client) {
        servers.get(name).jump(client);
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        for(String s : servers.keySet()) {
            json.put(s, servers.get(s).toJSONObject());
        }
        return json;
    }

    public JSONObject toJSONObjectExceptToken() {
        JSONObject json = new JSONObject();
        for(String s : servers.keySet()) {
            json.put(s, servers.get(s).toJSONObjectExceptToken());
        }
        return json;
    }

    public void updateToPlayer(ClientConnection connection) {
        connection.send(new CustomPayloadS2CPacket(modMdoServerChannel, new PacketByteBuf(Unpooled.buffer()).writeVarInt(107).writeString(toJSONObjectExceptToken().toString())));
    }

    public Collection<String> getServersName() {
        return servers.keySet();
    }

    public LinkedHashMap<String, ServerInformation> getServers() {
        return servers;
    }

    public boolean hasServer(String name) {
        return servers.containsKey(name);
    }

    public void remove(String name) {
        servers.remove(name);
    }
}
