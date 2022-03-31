package com.github.zhuaidadaya.modmdo.login.token;

import org.json.JSONObject;

public class ClientEncryptionToken extends EncryptionToken {
    public ClientEncryptionToken(String token, String address, String type,String modMdoVersion) {
        super(token, address, type,modMdoVersion);
    }

    public ClientEncryptionToken(JSONObject json) {
        super(json.getString("token"), json.getString("address"), json.getString("login_type"), json.getString("modmdo_version"));
    }

    public JSONObject toJSONObject() {
        return new JSONObject().put(getAddress(),new JSONObject().put("token",getToken()).put("login_type", getType()).put("address", getAddress()).put("modmdo_version", getVersion()));
    }
}
