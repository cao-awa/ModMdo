package com.github.zhuaidadaya.utils.config;

import com.github.zhuaidadaya.utils.config.AbstractConfig;
import it.unimi.dsi.fastutil.objects.ObjectList;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class ReformConfig<V> implements AbstractConfig {
    public V configValue;
    private final boolean listTag;

    /**
     * init, create a config
     *
     * @param v
     *         config value, use when return config
     * @param list
     *         list value, if true then value will be an array
     *         else then value will be a normally key:value
     *         <p>
     *         if <code>list = true</code> and quantity of value exceed 1
     *         config will make style:
     *
     *         <code>
     *         $Config : {
     *         <tab><tab>$Value1 : $Value2,
     *         <tab><tab>$Value3 : $Value4
     *         <tab><tab>......
     *         }
     *         </code>
     */
    public ReformConfig(V v, boolean list) {
        configValue = v;
        this.listTag = list;
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

        json.put("listTag", listTag);
        if(! listTag) {
            if(configValue instanceof Object[])
                json.put("value", List.of((Object[]) configValue).get(0));
            else if(configValue instanceof String)
                json.put("value", configValue.toString());
            else if(configValue instanceof Boolean)
                json.put("value", Boolean.parseBoolean(configValue.toString()));
            else if(configValue instanceof Integer)
                json.put("value", Integer.parseInt(configValue.toString()));
            else
                json.put("value", configValue);
        } else {
            ObjectList<Object> list;
            if(configValue instanceof Object[]) {
                list = ObjectList.of((Object[]) configValue);
            } else {
                list = ObjectList.of(configValue);
            }
            json.put("totalSize", list.size());
            json.put("values", list);
        }

        return json;
    }

    public String toString() {
        if(configValue != null) {
            if(((Object[]) configValue).length == 1) {
                return "Config(" + ((Object[]) configValue)[0] + ")";
            } else {
                if(! listTag) {
                    StringBuilder builder = new StringBuilder();
                    Object cacheObject1 = new Object();
                    Object cacheObject2;
                    int index = 0;
                    for(Object o : (Object[]) configValue) {
                        if(index == 1) {
                            cacheObject2 = o.toString();
                            builder.append(cacheObject1.toString()).append("=").append(cacheObject2.toString()).append(", ");
                            index = 0;
                        } else {
                            cacheObject1 = o.toString();
                            index++;
                        }
                    }

                    builder.replace(builder.length() - 2, builder.length(), "");

                    return "Config({" + builder + "})";
                } else {
                    return "Config(" + Arrays.toString(((Object[]) configValue)) + ")";
                }
            }
        } else {
            return "";
        }
    }
}