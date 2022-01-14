package com.github.zhuaidadaya.utils.config;

public interface AbstractConfigUtil {
    void set(Object key, Object... configKeysValues);
    void setList(Object key, Object... configKeysValues);
    void remove(Object key);
}
