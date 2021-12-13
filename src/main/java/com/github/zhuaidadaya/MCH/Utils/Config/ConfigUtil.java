package com.github.zhuaidadaya.MCH.utils.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Random;

public class ConfigUtil {
    /**
     *
     */
    private final LinkedHashMap<Object, Config<Object, Object>> configs = new LinkedHashMap<>();
    private final LinkedHashMap<Object, Object> utilConfigs = new LinkedHashMap<>();
    /**
     *
     */
    private Logger logger;
    private boolean encryption = false;
    /**
     * if true
     * run <code>writeConfig()</code> when config has updated
     */
    private boolean autoWrite = true;
    private String entrust;
    private String note;

    public ConfigUtil(String entrust) {
        utilConfigs.put("path", System.getProperty("user.dir"));
        utilConfigs.put("name", "settings.conf");
        utilConfigs.put("version", "1.1");
        logger = LogManager.getLogger("ConfigUtil/" + entrust);
        readConfig(true);
    }

    public ConfigUtil(String entrust, boolean storage) {
        if(storage) {
            utilConfigs.put("path", System.getProperty("user.dir"));
            utilConfigs.put("name", "settings.conf");
            utilConfigs.put("version", "1.1");
            logger = LogManager.getLogger("ConfigUtil/" + entrust);
            readConfig(true);
        } else {

        }
    }

    public ConfigUtil(String configPath, String entrust) {
        utilConfigs.put("path", configPath);
        utilConfigs.put("name", "settings.conf");
        utilConfigs.put("version", "1.1");
        logger = LogManager.getLogger("ConfigUtil/" + entrust);
        readConfig(true);
    }

    public ConfigUtil(String configPath, String configName, String entrust) {
        utilConfigs.put("path", configPath);
        utilConfigs.put("name", configName);
        utilConfigs.put("version", "1.1");
        this.entrust = entrust;
        logger = LogManager.getLogger("ConfigUtil/" + entrust);
        readConfig(true);
    }

    public ConfigUtil(String configPath, String configName, String configVersion, String entrust) {
        utilConfigs.put("path", configPath);
        utilConfigs.put("name", configName);
        utilConfigs.put("version", configVersion);
        this.entrust = entrust;
        logger = LogManager.getLogger("ConfigUtil/" + entrust);
        readConfig(true);
    }

    public ConfigUtil setEntrust(String entrust) {
        this.entrust = entrust;
        logger = LogManager.getLogger("ConfigUtil/" + entrust);
        return this;
    }

    public ConfigUtil setAutoWrite(boolean autoWrite) {
        this.autoWrite = autoWrite;
        return this;
    }

    public ConfigUtil setNote(String note) {
        this.note = note;
        return this;
    }

    public ConfigUtil fuse(ConfigUtil parent) {
        for(Object o : parent.configs.keySet())
            this.set(o.toString(), parent.configs.get(o.toString()).getValue());
        return this;
    }

    public ConfigUtil setEncryption(boolean encryption) {
        this.encryption = encryption;
        if(autoWrite) {
            try {
                writeConfig();
            } catch (Exception e) {

            }
        }
        return this;
    }

    public LinkedHashMap<Object, Config<Object, Object>> getConfigs() {
        return configs;
    }

    public Config<Object, Object> getConfig(Object conf) {
        return configs.get(conf);
    }

    public String getConfigValue(Object conf) {
        return getConfig(conf).getValue();
    }

    public void readConfig() {
        readConfig(false);
    }

    public void readConfig(boolean log) {
        int configSize = 0;
        try {
            logger.info("loading config from: " + utilConfigs.get("name").toString());

            JSONArray configs;

            int checkCode;
            boolean encrypted;
            File configFile = new File(utilConfigs.get("path").toString() + "/" + utilConfigs.get("name").toString());

            BufferedReader br = new BufferedReader(new FileReader(configFile, Charset.forName("unicode")));
            StringBuilder builder = new StringBuilder();
            String cache;

            encrypted = br.readLine().startsWith("encryption");
            if(encrypted) {
                checkCode = Integer.parseInt(String.valueOf(br.readLine().chars().toArray()[0]));

                while((cache = br.readLine()) != null) {
                    if(! cache.startsWith("/**") & ! cache.startsWith(" *") & ! cache.startsWith(" */"))
                        builder.append(cache);
                }

                StringBuilder s1 = new StringBuilder();
                int lim = builder.length() > 1 ? builder.charAt(0) : 0;
                builder = new StringBuilder(builder.length() > 1 ? builder.substring(1) : "");

                for(Object o : builder.chars().toArray())
                    s1.append((char) (Integer.parseInt(o.toString()) - lim - checkCode));

                configs = new JSONObject(s1.toString()).getJSONArray("configs");
                configSize = s1.length();
            } else {
                while(true) {
                    String startWith = br.readLine();
                    if(startWith.replace(" ", "").startsWith("{")) {
                        builder.append(startWith);
                        break;
                    }
                }
                while((cache = br.readLine()) != null) {
                    if(! cache.startsWith("/**") & ! cache.startsWith(" *") & ! cache.startsWith(" */"))
                        builder.append(cache);
                }

                configs = new JSONArray(new JSONObject(builder.toString()).getJSONArray("configs"));
                configSize = builder.length();
            }

            br.close();

            for(Object o : configs) {
                JSONObject config = new JSONObject(o.toString());
                String configKey = config.keySet().toArray()[0].toString();
                if(log)
                    logger.info("loading for config: " + configKey);
                JSONObject configDetailed = config.getJSONObject(configKey);
                if(configDetailed.getBoolean("listTag")) {
                    JSONArray array = configDetailed.getJSONArray("values");
                    LinkedList<Object> addToConfig = new LinkedList<>();
                    for(Object inArray : array)
                        addToConfig.add(inArray);
                    setList(configKey, addToConfig.toArray());
                } else {
                    set(configKey, configDetailed.get("value").toString());
                }
            }

            if(log)
                logger.info("load config done");
        } catch (Exception e) {
            logger.error("failed to load config: " + utilConfigs.get("name").toString(), e);
            File configFile = new File(utilConfigs.get("path").toString() + "/" + utilConfigs.get("name").toString());
            if(! configFile.isFile() || configFile.length() == 0 || configSize == 0) {
                try {
                    configFile.createNewFile();
                    writeConfig();
                    logger.info("created new config file for " + entrust);
                } catch (Exception ex) {
                    logger.error("failed to create new config file for " + entrust, ex);
                }
            }
        }
    }

    public void writeConfig() throws Exception {
        BufferedWriter writer = new BufferedWriter(new FileWriter(utilConfigs.get("path").toString() + "/" + utilConfigs.get("name").toString(), Charset.forName("unicode"), false));

        String write = this.toJSONObject().toString();

        Random r = new Random();
        int checkingCodeMax = 1024 * 8;
        int checkingCodeRange = r.nextInt(checkingCodeMax);
        int checkingCode = r.nextInt((checkingCodeRange / 8) > 0 ? checkingCodeRange / 8 : 16);
        writer.write(encryption ? "encryption: [check_code=" + checkingCode + ", range=" + checkingCodeRange + ", config_size=" + write.length() + ", config_version=" + utilConfigs.get("version") + "]\n" : "no encryption config: [config_size=" + write.length() + ", config_version=" + utilConfigs.get("version") + "]\n");
        writer.write(formatNote() + "\n\n");
        if(encryption) {
            writer.write(checkingCodeRange);
            writer.write("\n");
            writer.write(checkingCode);

            int[] charArray = write.chars().toArray();
            int count = 0;
            for(Object o : charArray) {
                count++;
                if(Integer.parseInt(o.toString()) == 10) {
                    int rand = r.nextInt((checkingCodeRange / 8) > 0 ? checkingCodeRange / 8 : 16);
                    writer.write(10);
                    checkingCode = rand > 13 ? rand : 14;
                    if(count != charArray.length)
                        writer.write(checkingCode);
                } else {
                    writer.write((Integer.parseInt(o.toString()) + checkingCode + checkingCodeRange));
                }
            }
        } else {
            writer.write(write);
        }

        writer.close();
    }

    public ConfigUtil set(Object key, Object... configKeysValues) throws IllegalArgumentException {
        if(configKeysValues.length > 1 & configKeysValues.length % 2 != 0)
            throw new IllegalArgumentException("values argument size need Integral multiple of 2, but argument size " + configKeysValues.length + " not Integral multiple of 2");
        configs.put(key, new Config<>(key, configKeysValues, false));
        if(autoWrite) {
            try {
                writeConfig();
            } catch (Exception e) {

            }
        }
        return this;
    }

    public ConfigUtil setList(Object key, Object... configValues) {
        configs.put(key, new Config<>(key, configValues, true));
        if(autoWrite) {
            try {
                writeConfig();
            } catch (Exception e) {

            }
        }
        return this;
    }


    public String toString() {
        StringBuilder builder = new StringBuilder();
        for(Object o : configs.keySet()) {
            builder.append(o.toString()).append("=").append(configs.get(o).toString()).append(", ");
        }

        try {
            builder.replace(builder.length() - 2, builder.length(), "");
        } catch (Exception e) {

        }

        return "ConfigUtil(" + builder + ")";
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        JSONArray addToConfig = new JSONArray();
        for(Object o : configs.keySet()) {
            addToConfig.put(configs.get(o.toString()).toJSONObject());
        }

        json.put("configs", addToConfig);

        JSONObject manifest = new JSONObject();
        manifest.put("configVersion", utilConfigs.get("version"));
        manifest.put("configsTotal", configs.size());
        manifest.put("encryption", encryption);
        manifest.put("config", new File(utilConfigs.get("path") + "/" + utilConfigs.get("name")).getAbsolutePath());
        json.put("manifest", manifest);

        return json;
    }

    public boolean equal(ConfigUtil configUtil1, ConfigUtil configUtil2) {
        return configUtil1.toString().equals(configUtil2.toString());
    }

    public boolean equal(ConfigUtil configUtil) {
        return configUtil.toString().equals(this.toString());
    }

    public String formatNote() {
        if(note != null) {
            try {
                BufferedReader reader = new BufferedReader(new StringReader(note));
                StringBuilder builder = new StringBuilder("/**\n");

                String cache;
                while((cache = reader.readLine()) != null)
                    builder.append(" * ").append(cache).append("\n");
                builder.append(" */");

                return builder.toString();
            } catch (Exception e) {
                return "";
            }
        } else {
            return "";
        }
    }
}
