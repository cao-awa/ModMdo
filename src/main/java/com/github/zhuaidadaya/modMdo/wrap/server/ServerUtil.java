package com.github.zhuaidadaya.modMdo.wrap.server;

import net.minecraft.client.MinecraftClient;
import org.json.JSONObject;

import java.util.Collection;
import java.util.LinkedHashMap;

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

    public ServerInformation getServer(String name) {
        return servers.get(name);
    }

    public void wrap(String name, MinecraftClient client) {
        servers.get(name).wrap(client);
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        for(String s : servers.keySet()) {
            json.put(s, servers.get(s).toJSONObject());
        }
        return json;
    }

    public Collection<String> getServersName() {
        return servers.keySet();
    }

    public LinkedHashMap<String,ServerInformation> getServers() {
        return servers;
    }

    public boolean hasServer(String name) {
        return servers.containsKey(name);
    }

    public void remove(String name) {
        servers.remove(name);
    }
}
