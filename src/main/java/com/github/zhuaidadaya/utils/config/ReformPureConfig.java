package com.github.zhuaidadaya.utils.config;

import it.unimi.dsi.fastutil.objects.ObjectList;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class ReformPureConfig<V> implements AbstractConfig {
    public V configValue;

    /**
     * init, create a config
     *
     * @param v
     *         config value, use when return config
     */
    public ReformPureConfig(V v) {
        configValue = v;
    }

    public static void main(String[] args) {
        ReformPureConfig<Object> c = new ReformPureConfig<>("test");

        System.out.println(c.toJSONObject());
    }

    public Object getValue() {
        return configValue;
    }

    public String getString() {
        try {
            return getValue().toString();
        } catch (Exception e) {
            return null;
        }
    }

    public int getInt() {
        return Integer.parseInt(getString());
    }

    public long getLong() {
        return Long.parseLong(getString());
    }

    public boolean getBoolean() {
        String value = getString();
        if(value.equals("true"))
            return true;
        if(value.equals("false"))
            return false;
        throw new IllegalArgumentException("cannot cast \"" + value + "\" to a boolean value");
    }

    public JSONObject getJSONObject() {
        return new JSONObject(getValue().toString());
    }

    public JSONArray getJSONArray() {
        return new JSONArray(getValue().toString());
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();

        if(configValue instanceof Object[] | configValue instanceof List<?>) {
            ObjectList<Object> list;
            if(configValue instanceof Object[]) {
                list = ObjectList.of((Object[]) configValue);

                if(list.size() == 1)
                    list = ObjectList.of(ObjectList.of((Object[]) configValue).get(0));

                json.put("values", list);
            } else {
                list = ObjectList.of(configValue);

                json.put("values", (List<?>) configValue);
            }

            json.put("totalSize", list.size());

            json.put("listTag", true);
        } else {
            if(configValue instanceof String)
                json.put("value", configValue.toString());
            else if(configValue instanceof Boolean)
                json.put("value", Boolean.parseBoolean(configValue.toString()));
            else if(configValue instanceof Integer)
                json.put("value", Integer.parseInt(configValue.toString()));
            else
                json.put("value", configValue);

            json.put("listTag", false);
        }

        return json;
    }

    public String toString() {
        if(configValue != null) {
            if(((Object[]) configValue).length == 1) {
                return "Config(" + ((Object[]) configValue)[0] + ")";
            } else {
                return "Config(" + Arrays.toString(((Object[]) configValue)) + ")";
            }
        } else {
            return "";
        }
    }
}