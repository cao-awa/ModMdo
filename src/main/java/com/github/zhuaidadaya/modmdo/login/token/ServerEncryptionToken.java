package com.github.zhuaidadaya.modmdo.login.token;

import com.github.zhuaidadaya.modmdo.login.token.Encryption.AES;
import org.json.JSONObject;

import java.util.Objects;

public class ServerEncryptionToken extends EncryptionToken {
    public ServerEncryptionToken(String serverDefaultToken, String serverOpsToken) {
        super(Objects.requireNonNull(serverDefaultToken), Objects.requireNonNull(serverOpsToken));
    }

    public ServerEncryptionToken(EncryptionToken serverToken) {
        super(serverToken.getServerDefaultToken(), serverToken.getServerOpsToken());
    }

    public ServerEncryptionToken(JSONObject json) {
        super(json.getString("default"), json.getString("ops"));
    }

    public static ServerEncryptionToken createServerEncryptionToken() {
        try {
            return new ServerEncryptionToken(new AES().randomGet(1024), new AES().randomGet(1024));
        } catch (Exception e) {
            return new ServerEncryptionToken("", "");
        }
    }

    public static ServerEncryptionToken createServerEncryptionToken(int size) throws Exception {
        return new ServerEncryptionToken(new AES().randomGet(size), new AES().randomGet(size));
    }

    public JSONObject toJSONObject() {
        return new JSONObject().put("default", getServerDefaultToken()).put("ops", getServerOpsToken());
    }
}