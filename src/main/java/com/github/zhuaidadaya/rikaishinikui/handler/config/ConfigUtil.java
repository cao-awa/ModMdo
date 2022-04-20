package com.github.zhuaidadaya.rikaishinikui.handler.config;

import org.json.JSONArray;
import org.json.JSONObject;

public interface ConfigUtil {
    void set(String key, Object configKeysValues);

    void remove(String key);

    Object getConfig(String key);

    String getConfigString(String key);

    Boolean getConfigBoolean(String key);

    Integer getConfigInt(String config);

    Long getConfigLong(String config);

    JSONObject getConfigJSONObject(String config);

    JSONArray getConfigJSONArray(String config);
}
