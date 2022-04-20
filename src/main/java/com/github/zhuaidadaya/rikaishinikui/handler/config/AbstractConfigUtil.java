package com.github.zhuaidadaya.rikaishinikui.handler.config;

public interface AbstractConfigUtil {
    void set(Object key, Object... configKeysValues);
    void setList(Object key, Object... configKeysValues);
    void remove(Object key);
}
