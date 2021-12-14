package com.github.zhuaidadaya.modMdo.token;

public class EncryptionToken {
    private String token = "";
    private String address = "";
    private String type = "default";

    private String serverDefaultToken = "";
    private String serverOpsToken = "";

    public EncryptionToken(String token, String address, String type) {
        this.token = token;
        this.address = address;
        this.type = type;
    }

    public EncryptionToken(String defaultToken, String opsToken) {
        this.serverDefaultToken = defaultToken;
        this.serverOpsToken = opsToken;
    }

    public String getToken() {
        return this.token;
    }

    public EncryptionToken setToken(String token) {
        this.token = token;
        return this;
    }

    public String checkToken(String checkType) {
        if(checkType.equals("default"))
            return this.serverDefaultToken;
        else
            return this.serverOpsToken;
    }

    public String getAddress() {
        return this.address;
    }

    public EncryptionToken setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getType() {
        return this.type;
    }

    public EncryptionToken setType(String type) {
        this.type = type;
        return this;
    }

    public String getServerDefaultToken() {
        return serverDefaultToken;
    }

    public EncryptionToken setServerDefaultToken(String serverDefaultToken) {
        this.serverDefaultToken = serverDefaultToken;
        return this;
    }

    public String getServerOpsToken() {
        return serverOpsToken;
    }

    public EncryptionToken setServerOpsToken(String serverOpsToken) {
        this.serverOpsToken = serverOpsToken;
        return this;
    }
}
