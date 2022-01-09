package com.github.zhuaidadaya.MCH.utils.config;

import org.json.JSONObject;

import java.util.Arrays;

public class Config<K, V> {
    private final K configKey;
    private final V configValue;
    private final boolean listTag;

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
        this.listTag = list;
    }

    public String getKey() {
        return configKey.toString();
    }

    public String getValue() {
        JSONObject json = toJSONObject();
        try {
            return json.getJSONObject(json.keySet().toArray()[0].toString()).get("value").toString();
        } catch (Exception e) {
            return json.getJSONObject(json.keySet().toArray()[0].toString()).getJSONArray("values").toString();
        }
    }

    public JSONObject toJSONObject() {
        if(configValue != null) {
            JSONObject json = new JSONObject();
            JSONObject values = new JSONObject();
            Object[] configValues = (Object[]) configValue;
            if(configValues.length == 1) {
                JSONObject inJ = new JSONObject();
                inJ.put("listTag", listTag);
                if(listTag)
                    inJ.put("values", configValues);
                else
                    inJ.put("value", configValues[0]);
                json.put(getKey(), inJ);
            } else {
                if(! listTag) {
                    int index = 0;
                    Object cacheObject = new Object();
                    Object cacheObject2 = new Object();
                    for(Object o : configValues) {
                        if(index == 1) {
                            cacheObject2 = o;
                            values.put(cacheObject.toString(), cacheObject2);
                            index = 0;
                        } else {
                            cacheObject = o;
                            index++;
                        }
                    }

                    if(index % 2 != 0) {
                        json.put(getKey(), new JSONObject().put("listTag", false).put("value", configValues));
                    } else {
                        values.put(cacheObject.toString(), cacheObject2);
                        json.put(getKey(), new JSONObject().put("listTag", false).put("value", values));
                    }
                } else {
                    json.put(getKey(), new JSONObject().put("listTag", true).put("values", configValues).put("totalSize", configValues.length));
                }
            }
            return json;
        } else {
            return new JSONObject();
        }
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
