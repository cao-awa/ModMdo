package com.github.zhuaidadaya.modmdo.utils.config;

public interface AbstractConfigUtil {
    void set(Object key, Object... configKeysValues);
    void setList(Object key, Object... configKeysValues);
    void remove(Object key);
}
