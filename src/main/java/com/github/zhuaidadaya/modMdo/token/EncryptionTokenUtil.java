package com.github.zhuaidadaya.modMdo.token;

import org.json.JSONObject;

import java.util.LinkedHashMap;

public class EncryptionTokenUtil {
    private final LinkedHashMap<String, ClientEncryptionToken> clientTokens = new LinkedHashMap<>();
    private ServerEncryptionToken serverToken;

    public EncryptionTokenUtil() {

    }

    public EncryptionTokenUtil(ClientEncryptionToken clientToken) {
        addClientToken(clientToken);
    }

    public EncryptionTokenUtil(ServerEncryptionToken serverToken) {
        setServerToken(serverToken);
    }

    public EncryptionTokenUtil addClientToken(ClientEncryptionToken clientToken) {
        clientTokens.put(clientToken.getAddress(), clientToken);
        return this;
    }

    public ClientEncryptionToken getClientToken(String address) {
        return clientTokens.get(address);
    }

    public ServerEncryptionToken getServerToken() {
        return serverToken;
    }

    public EncryptionTokenUtil setServerToken(ServerEncryptionToken serverToken) {
        this.serverToken = serverToken;
        return this;
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        JSONObject server = new JSONObject();
        JSONObject client = new JSONObject();
        try {
            server.put("default", serverToken.getServerDefaultToken());
            server.put("ops", serverToken.getServerOpsToken());
        } catch (Exception e) {

        }
        json.put("server", server);

        for(ClientEncryptionToken clientToken : clientTokens.values()) {
            JSONObject clientTokenContent = new JSONObject();
            clientTokenContent.put("token", clientToken.getToken());
            clientTokenContent.put("login_type", clientToken.getType());
            client.put(clientToken.getAddress(), clientTokenContent);
        }
        json.put("client", client);

        return json;
    }
}
