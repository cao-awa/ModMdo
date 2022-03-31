package com.github.zhuaidadaya.modmdo.type;

public enum ModMdoType {
    NONE(- 1, "None"), CLIENT(0, "Client"), SERVER(1, "Server");

    final int id;
    final String type;

    ModMdoType(int id, String type) {
        this.id = id;
        this.type = type;
    }
}
