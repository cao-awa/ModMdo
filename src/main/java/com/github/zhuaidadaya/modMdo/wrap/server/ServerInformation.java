package com.github.zhuaidadaya.modMdo.wrap.server;

import net.minecraft.client.MinecraftClient;
import org.json.JSONObject;

public class ServerInformation {
    private final ServerWrap serverWrap = new ServerWrap();
    private String host = "127.0.0.1";
    private int port = 25565;
    private String name = "server";
    private boolean error = false;

    public ServerInformation() {

    }

    public ServerInformation(String host, int port, String name) {
        this.host = host;
        this.port = port;
        this.name = name;
    }


    public ServerInformation(JSONObject json) {
        this.host = json.getString("host");
        this.port = json.getInt("port");
        this.name = json.getString("name");
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public boolean isError() {
        return error;
    }

    public void wrap(MinecraftClient client) {
        serverWrap.wrap(host, port, client);
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        json.put("host", host);
        json.put("port", port);
        json.put("name", name);
        return json;
    }
}
