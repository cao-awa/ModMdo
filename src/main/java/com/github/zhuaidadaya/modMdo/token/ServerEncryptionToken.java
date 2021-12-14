package com.github.zhuaidadaya.modMdo.token;

import com.github.zhuaidadaya.modMdo.token.Encryption.AES;
import org.json.JSONObject;

public class ServerEncryptionToken extends EncryptionToken{
    public ServerEncryptionToken(String token, String address, String type) {
        super(token, address, type);
    }

    public ServerEncryptionToken(String serverDefaultToken,String serverOpsToken) {
        super(serverDefaultToken,serverOpsToken);
    }

    public JSONObject toJSONObject() {
        return new JSONObject().put("default", getToken());
    }

    public static ServerEncryptionToken createServerEncryptionToken() {
        try {
            return new ServerEncryptionToken(new AES().randomGet(128), new AES().randomGet(128));
        } catch (Exception e) {
            return new ServerEncryptionToken("","");
        }
    }
}
