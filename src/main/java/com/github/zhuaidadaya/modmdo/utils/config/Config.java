package com.github.zhuaidadaya.modmdo.utils.config;

import it.unimi.dsi.fastutil.objects.ObjectList;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class Config<K, V> implements AbstractConfig {
    private K configKey;
    public V configValue;
    private boolean listTag;

    /**
     * init, create a config
     *
     * @param k
     *         config name, use when get config
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
    public Config(K k, V v, boolean list) {
        configKey = k;
        configValue = v;
        listTag = list;
    }
//
//    public Config<K, V> clone() {
//        return clone(configKey,configValue,listTag);
//    }
//
//    public Config<K, V> clone(K k, V v, boolean list) {
//        try {
//            Config<K, V> c = (Config<K, V>) super.clone();
//            c.configKey = k;
//            c.configValue = v;
//            c.listTag = list;
//            return c;
//        } catch (CloneNotSupportedException e) {
//            throw new InternalError(e);
//        }
//    }

    public String getKey() {
        return configKey.toString();
    }

    public Object getValue() {
        JSONObject json = toJSONObject();
        try {
            return json.getJSONObject(json.keySet().toArray()[0].toString()).get("value");
        } catch (Exception e) {
            return json.getJSONObject(json.keySet().toArray()[0].toString()).getJSONArray("values");
        }
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
        JSONObject inJ = new JSONObject();

        inJ.put("listTag", listTag);
        if(! listTag) {
            if(configValue instanceof Object[])
                inJ.put("value", List.of((Object[]) configValue).get(0));
            else if(configValue instanceof String)
                inJ.put("value", configValue.toString());
            else if(configValue instanceof Boolean)
                inJ.put("value", Boolean.parseBoolean(configValue.toString()));
            else if(configValue instanceof Integer)
                inJ.put("value", Integer.parseInt(configValue.toString()));
            else
                inJ.put("value", configValue);
        } else {
            ObjectList<Object> list;
            if(configValue instanceof Object[]) {
                list = ObjectList.of((Object[]) configValue);
            } else {
                list = ObjectList.of(configValue);
            }
            inJ.put("totalSize", list.size());
            inJ.put("values", list);
        }

        json.put(getKey(), inJ);

        return json;
    }

    public String toString() {
        if(configValue != null) {
            if(((Object[]) configValue).length == 1) {
                return "Config(" + configKey + "=" + ((Object[]) configValue)[0] + ")";
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

                    return "Config(" + configKey + "={" + builder + "})";
                } else {
                    return "Config(" + configKey + "=" + Arrays.toString(((Object[]) configValue)) + ")";
                }
            }
        } else {
            return "";
        }
    }
}