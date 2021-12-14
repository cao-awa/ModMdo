package com.github.zhuaidadaya.modMdo.token;

import org.json.JSONObject;

public class ClientEncryptionToken extends EncryptionToken{
    public ClientEncryptionToken(String token, String address, String type) {
        super(token, address, type);
    }

    public JSONObject toJSONObject() {
        return new JSONObject().put(this.getAddress(),new JSONObject().put("token",getToken()).put("login_type", getType()));
    }

}
