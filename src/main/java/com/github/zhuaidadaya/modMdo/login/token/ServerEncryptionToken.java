package com.github.zhuaidadaya.modMdo.login.token;

import com.github.zhuaidadaya.modMdo.login.token.Encryption.AES;
import org.json.JSONObject;

public class ServerEncryptionToken extends EncryptionToken {
    public ServerEncryptionToken(String serverDefaultToken, String serverOpsToken) {
        super(serverDefaultToken, serverOpsToken);
    }

    public ServerEncryptionToken(EncryptionToken serverToken) {
        super(serverToken.getServerDefaultToken(), serverToken.getServerOpsToken());
    }

    public static ServerEncryptionToken createServerEncryptionToken() {
        try {
            return new ServerEncryptionToken(new AES().randomGet(128), new AES().randomGet(128));
        } catch (Exception e) {
            return new ServerEncryptionToken("", "");
        }
    }

    public static ServerEncryptionToken createServerEncryptionToken(int size) throws Exception {
        return new ServerEncryptionToken(new AES().randomGet(size), new AES().randomGet(size));
    }

    public JSONObject toJSONObject() {
        return new JSONObject().put("default", getToken());
    }
}
