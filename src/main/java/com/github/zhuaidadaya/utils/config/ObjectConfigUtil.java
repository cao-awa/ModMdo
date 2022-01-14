package com.github.zhuaidadaya.utils.config;

import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ObjectConfigUtil implements AbstractConfigUtil {
    /**
     *
     */
    private final Object2ObjectMap<Object, Object> configs = new Object2ObjectRBTreeMap<>();
    private EncryptionType encryptionType = EncryptionType.COMPOSITE_SEQUENCE;
    /**
     *
     */
    private Logger logger = LogManager.getLogger("ConfigUtil");
    /**
     * if true<br>
     * run <code>writeConfig()</code> when config has updated
     */
    private boolean empty = false;
    private int splitRange = 20;
    private int libraryOffset = 5;
    private boolean canShutdown = true;
    private boolean shuttingDown = false;
    private boolean shutdown = false;

    public ObjectConfigUtil() {
        defaultUtilConfigs();
        readConfig(true);
    }

    public ObjectConfigUtil(String entrust) {
        defaultUtilConfigs();
        logger = LogManager.getLogger("ConfigUtil-" + entrust);
        readConfig(true);
    }

    public ObjectConfigUtil(String entrust, String configPath) {
        defaultUtilConfigs();
        setPath(configPath);
        logger = LogManager.getLogger("ConfigUtil-" + entrust);
        readConfig(true);
    }

    public ObjectConfigUtil(String entrust, String configPath, String configName) {
        defaultUtilConfigs();
        setPath(configPath);
        setName(configName);
        setEntrust(entrust);
        logger = LogManager.getLogger("ConfigUtil-" + entrust);
        readConfig(true);
    }

    public ObjectConfigUtil(String entrust, String configPath, String configName, String configVersion) {
        defaultUtilConfigs();
        setPath(configPath);
        setName(configName);
        setVersion(configVersion);
        setEntrust(entrust);
        logger = LogManager.getLogger("ConfigUtil-" + entrust);
        readConfig(true);
    }

    public ObjectConfigUtil(String entrust, String configPath, String configName, String configVersion, boolean empty) {
        defaultUtilConfigs();
        setPath(configPath);
        setName(configName);
        setVersion(configVersion);
        setEntrust(entrust);
        logger = LogManager.getLogger("ConfigUtil-" + entrust);
        this.empty = empty;
        if(! empty)
            readConfig(true);
    }

    public ObjectConfigUtil(String entrust, String configPath, String configName, String configVersion, boolean empty, boolean loadManifest) {
        defaultUtilConfigs();
        setPath(configPath);
        setName(configName);
        setVersion(configVersion);
        setEntrust(entrust);
        logger = LogManager.getLogger("ConfigUtil-" + entrust);
        this.empty = empty;
        if(! empty)
            readConfig(true, false, loadManifest);
    }

    public static void main(String[] args) {
        ObjectConfigUtil config = new ObjectConfigUtil("CU", "config/", "test_obj_pure.mhf", "1.1") //
                .setEncryptionType(EncryptionType.COMPOSITE_SEQUENCE) //
                .setLibraryOffset(100) //
                .setSplitRange(5000) //
                .setEncryption(true) //
                .setEncryptionHead(true) //
                .setInseparableLevel(3); //
        //                ;

        Logger logger = LogManager.getLogger("Teat");

        config.setAutoWrite(false);

        Random r = new SecureRandom();
        int limit = 8300;

        //        int count = config.getConfigTotal() - 100;
        int count = 0;
        //        System.out.println(config.get("test"));

        config.setList("test", "teeeeeeeeeeeeeeeeeeeeeeeeest");
        while(true) {
            try {
                count++;
                long startTime = System.nanoTime();
                //                config.readConfig();
                //                logger.info("read done in " + (System.currentTimeMillis() - startTime) + "ms, load " + config.getConfigTotal() + " configs");
                //                                config.set("test" + r.nextInt(limit), "teeeeeeeeeeeeeeeeeeeeeeeeest" + r.nextInt(limit));
                config.set("test" + count, "teeeeeeeeeeeeeeeeeeeeeeeeest" + count);
                //                                config.writeConfig();
                //                                logger.info("write done in " + (System.currentTimeMillis() - startTime) + "ms, load " + config.getConfigTotal() + " configs");
                logger.info("set done in " + (double) (System.nanoTime() - startTime) / 1000000d + "ms, load " + config.getConfigTotal() + " configs");
                //                startTime = System.nanoTime();
                //                System.out.println(config.getConfigString("test" + r.nextInt(count)));
                //                logger.info("query done in " + (double) (System.nanoTime() - startTime) / 1000000d + "ms, load " + config.getConfigTotal() + " configs");

                if(count > 10000) {
                    config.shutdown();

                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.info("test failed after " + count + ", CU have " + config.getConfigTotal() + " configs");
                break;
            }
        }
    }

    public static ObjectConfigUtil emptyConfigUtil() {
        return new ObjectConfigUtil(null, null, "1.1", null, true);
    }

    public ObjectConfigUtil setPath(String path) {
        addUtilConfig("path", path);
        return this;
    }

    public ObjectConfigUtil setVersion(String version) {
        addUtilConfig("version", version);
        return this;
    }

    public ObjectConfigUtil setName(String name) {
        try {
            name.substring(name.indexOf("."), name.indexOf(".") + 1);
            addUtilConfig("name", name);
        } catch (Exception e) {
            addUtilConfig("name", name + (String.valueOf(name.charAt(name.length() - 1)).equals(".") ? "mhf" : ".mhf"));
        }
        return this;
    }

    public ObjectConfigUtil setPath(File path) {
        return setPath(path.getPath());
    }

    public void defaultUtilConfigs() {
        addUtilConfig("path", System.getProperty("user.dir"));
        addUtilConfig("name", "config.mhf");
        addUtilConfig("version", "1.2");
        addUtilConfig("autoWrite", true);
        addUtilConfig("inseparableLevel", 3);
        addUtilConfig("encryptionHead", false);
        addUtilConfig("encryption", false);
    }

    public void addUtilConfig(Object name, Object value) {
        configs.put("CU%" + name, value);
    }

    public ObjectConfigUtil setInseparableLevel(int inseparableLevel) {
        configs.put("CU%inseparableLevel", inseparableLevel > - 1 ? inseparableLevel < 4 ? inseparableLevel : 3 : 0);
        return this;
    }

    public ObjectConfigUtil setLibraryOffset(int offset) {
        if(offset != - 1)
            this.libraryOffset = Math.max(1, offset);
        else
            this.libraryOffset = 1024;
        return this;
    }

    public ObjectConfigUtil setSplitRange(int range) {
        splitRange = range;
        return this;
    }

    public ObjectConfigUtil setEncryptionType(EncryptionType type) {
        this.encryptionType = type;
        return this;
    }

    public ObjectConfigUtil setEmpty(boolean empty) {
        this.empty = empty;
        return this;
    }

    public ObjectConfigUtil setEntrust(String entrust) {
        addUtilConfig("entrust", entrust);
        logger = LogManager.getLogger("ConfigUtil-" + entrust);
        return this;
    }

    public ObjectConfigUtil setEncryptionHead(boolean encryptionHead) {
        addUtilConfig("encryptionHead", encryptionHead);
        return this;
    }

    public ObjectConfigUtil setAutoWrite(boolean autoWrite) {
        configs.put("CU%autoWrite", autoWrite);
        return this;
    }

    public ObjectConfigUtil setNote(String note) {
        addUtilConfig("note", note);
        return this;
    }

    public ObjectConfigUtil fuse(ObjectConfigUtil parent) {
        for(Object o : parent.configs.keySet())
            this.setConf(true, o.toString(), parent.configs.get(o.toString()));
        return this;
    }

    public ObjectConfigUtil setEncryption(boolean encryption) {
        addUtilConfig("encryption", encryption);
        if(getUtilBoolean("autoWrite")) {
            try {
                writeConfig();
            } catch (Exception e) {

            }
        }
        return this;
    }

    public Map<Object, Object> getConfigs() {
        return configs;
    }

    public Object getConfig(Object conf) {
        return configs.get(conf);
    }

    public boolean readConfig() {
        return readConfig(false);
    }

    public boolean readConfig(boolean log) {
        return readConfig(log, false, false);
    }

    public boolean readConfig(boolean log, boolean forceLoad, boolean init) {
        if(shuttingDown) {
            return false;
        }
        canShutdown = false;

        if(empty) {
            canShutdown = true;

            return false;
        }
        int configSize = 0;
        try {
            if(log)
                logger.info("loading config from: " + getUtilString("name"));

            File configFile = new File(getUtilString("path") + "/" + getUtilString("name"));

            BufferedReader br = new BufferedReader(new FileReader(configFile, Charset.forName("unicode")));
            StringBuilder builder = decryption(br, forceLoad);

            br.close();

            JSONObject source;
            try {
                source = new JSONObject(builder.toString());
            } catch (Exception e) {
                canShutdown = true;

                return false;
            }
            JSONArray configs = source.getJSONArray("configs");
            configSize = builder.length();

            if(log)
                logger.info("loading configs");

            long start = System.nanoTime();

            for(Object o : configs) {
                if(! init & o.toString().contains("CU%")) {
                    continue;
                }
                JSONObject config = new JSONObject(o.toString());
                String configKey = config.keySet().toArray()[0].toString();
                if(log)
                    logger.info("loading for config: " + configKey);
                JSONObject configDetailed = config.getJSONObject(configKey);
                if(configDetailed.getBoolean("listTag")) {
                    JSONArray array = configDetailed.getJSONArray("values");
                    ObjectBigArrayBigList<Object> addToConfig = new ObjectBigArrayBigList<>();
                    for(Object inArray : array)
                        addToConfig.add(inArray);
                    setListConf(true, configKey, addToConfig);
                } else {
                    setConf(true, configKey, configDetailed.get("value").toString());
                }
            }

            logger.info("configs parse done, in " + (float) (System.nanoTime() - start) / 1000000f + "ms");

            if(init) {
                if(log)
                    logger.info("loading manifest");

                JSONObject manifest = source.getJSONObject("manifest");
                for(String s : manifest.keySet()) {
                    addUtilConfig(s, manifest.get(s));
                }
            }

            if(log)
                logger.info("load config done");

            canShutdown = true;

            return true;
        } catch (IllegalArgumentException e) {
            canShutdown = true;

            throw e;
        } catch (Exception e) {
            if(! shuttingDown) {
                logger.error(empty ? ("failed to load config") : ("failed to load config: " + getUtilString("name")));
                if(! empty) {
                    File configFile = new File(getUtilString("path") + "/" + getUtilString("name"));
                    if(! configFile.isFile() || configFile.length() == 0 || configSize == 0) {
                        try {
                            configFile.getParentFile().mkdirs();
                            configFile.createNewFile();
                            writeConfig();
                            logger.info("created new config file for " + getUtilString("entrust"));
                        } catch (Exception ex) {
                            logger.error("failed to create new config file for " + getUtilString("entrust"));
                        }
                    }
                }
            }

            canShutdown = true;

            return false;
        }
    }

    public StringBuilder decryption() {
        try {
            File configFile = new File(getUtilString("path") + "/" + getUtilString("name"));

            BufferedReader br = new BufferedReader(new FileReader(configFile, Charset.forName("unicode")));

            return decryption(br, false);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {

        }

        return null;
    }

    public StringBuilder decryption(BufferedReader reader, boolean forceLoad) {
        try {
            StringBuilder builder = new StringBuilder();
            String cache = reader.readLine();
            if(cache == null) {
                return null;
            }
            int encryptionType = cache.chars().toArray()[0];
            String encryptionEnable = cache.substring(2);
            boolean encrypted = encryptionEnable.startsWith("encryption") | encryptionEnable.startsWith("MCH DB");
            if(encrypted) {
                switch(encryptionType) {
                    case 0 -> {
                        while((cache = reader.readLine()) != null) {
                            if(! cache.startsWith("/**") & ! cache.startsWith(" *") & ! cache.startsWith(" */")) {
                                if(cache.length() > 0)
                                    builder.append(cache).append("\n");
                            }
                        }

                        int checkCode = Integer.parseInt(String.valueOf(builder.chars().toArray()[0]));

                        BufferedReader configRead = new BufferedReader(new StringReader(builder.toString()));

                        StringBuilder s1 = new StringBuilder();
                        while((cache = configRead.readLine()) != null) {
                            int lim = cache.length() > 1 ? cache.chars().toArray()[0] : 0;

                            boolean checkSkip = false;

                            for(Object o : cache.chars().toArray()) {
                                if(checkSkip) {
                                    int details = Integer.parseInt(o.toString());
                                    if(details != 10) {
                                        s1.append((char) (details - lim - checkCode));
                                    }
                                }
                                checkSkip = true;
                            }
                        }

                        return s1;
                    }
                    case 1 -> {
                        while((cache = reader.readLine()) != null) {
                            if(! cache.startsWith("/**") & ! cache.startsWith(" *") & ! cache.startsWith(" */")) {
                                if(cache.length() > 0)
                                    builder.append(cache).append("\n");
                            }
                        }

                        int checkCode = Integer.parseInt(String.valueOf(builder.chars().toArray()[0]));

                        BufferedReader configRead = new BufferedReader(new StringReader(builder.toString()));

                        while((cache = configRead.readLine()) != null) {
                            if(cache.startsWith("LIBRARY ")) {
                                break;
                            }
                        }

                        Int2IntMap libraryMap = new Int2IntOpenHashMap();

                        StringBuilder libraryInformation = new StringBuilder();

                        while((cache = configRead.readLine()) != null) {
                            if(cache.startsWith("INFORMATION ")) {
                                break;
                            }
                            libraryInformation.append(cache.replace("\b", "\n")).append("\n");
                        }

                        BufferedReader libraryRead = new BufferedReader(new StringReader(libraryInformation.toString()));

                        while((cache = libraryRead.readLine()) != null) {
                            int headCode = cache.chars().toArray()[0];
                            String[] libraryLine = cache.substring(1).split("\t");
                            for(String s : libraryLine) {
                                StringBuilder charCode = new StringBuilder();
                                int signCode = - 1;
                                boolean in = false;
                                for(int i : s.chars().toArray()) {
                                    if(! in) {
                                        signCode = i;
                                        in = true;
                                        continue;
                                    }
                                    charCode.append((char) (i - checkCode - headCode));
                                }
                                if(! charCode.toString().equals(""))
                                    libraryMap.put(signCode, Integer.parseInt(charCode.toString()));
                            }
                        }

                        libraryRead.close();

                        StringBuilder information = new StringBuilder();

                        while((cache = configRead.readLine()) != null) {
                            information.append(cache.substring(1));
                        }

                        information = new StringBuilder(information.toString().replace("\t", ""));

                        StringBuilder recodeInformation = new StringBuilder();

                        try {
                            for(int i : information.chars().toArray()) {
                                recodeInformation.append((char) libraryMap.get(i));
                            }
                        } catch (Exception e) {

                        }

                        return recodeInformation;
                    }
                    default -> {
                        if(! forceLoad)
                            throw new IllegalArgumentException("unsupported encryption type: " + encryptionType);
                    }
                }

            } else {
                while(true) {
                    String startWith = reader.readLine();
                    if(startWith.replace(" ", "").startsWith("{")) {
                        builder.append(startWith);
                        break;
                    }
                }
                while((cache = reader.readLine()) != null) {
                    if(! cache.startsWith("/**") || cache.startsWith(" *") || cache.startsWith(" */"))
                        builder.append(cache);
                }

                return builder;
            }
        } catch (IOException e) {

        }

        return null;
    }

    public void writeConfig() {
        try {
            if(shuttingDown) {
                return;
            }

            canShutdown = false;

            StringBuilder write = new StringBuilder(this.toJSONObject().toString());

            StringBuilder builder = new StringBuilder();

            Random r = new Random();

            if(getUtilBoolean("encryption")) {
                switch(encryptionType.getId()) {
                    case 0 -> {
                        builder = encryptionByRandomSequence(write, r);
                    }
                    case 1 -> {
                        builder = encryptionByCompositeSequence(write);
                    }
                }
            } else {
                builder = new StringBuilder();
                builder.append("no encryption config: [config_size=").append(write.length()).append(", config_version=").append(getUtilString("version")).append("]").append("\n").append(write);
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(getUtilString("path") + "/" + getUtilString("name"), Charset.forName("unicode"), false));
            write(writer, builder.toString());
            writer.close();

            canShutdown = true;
        } catch (Exception e) {
            canShutdown = true;
        }
    }

    public void write(Writer writer, StringBuffer information) throws IOException {
        writer.write(information.toString());
    }

    public void write(Writer writer, String information) throws IOException {
        write(writer, new StringBuffer(information));
    }

    public void write(StringBuffer information) throws IOException {
        write(information.toString());
    }

    public void write(String information) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(getUtilString("path") + "/" + getUtilString("name"), Charset.forName("unicode"), false));
        write(writer, new StringBuffer(information));
        writer.close();
    }

    public StringBuilder encryptionByRandomSequence(StringBuilder write, Random r) {
        int checkingCodeRange = r.nextInt(1024 * 8);
        checkingCodeRange = checkingCodeRange > 13 ? checkingCodeRange : 14;
        int checkingCode = r.nextInt(checkingCodeRange);
        checkingCode = checkingCode > 13 ? checkingCode : 14;
        int split = 0;

        StringBuilder builder = new StringBuilder();

        if(getUtilBoolean("encryption")) {
            int wrap = splitRange;

            for(; wrap > 0; wrap--) {
                int splitIndex = r.nextInt(100);
                if(splitIndex < 50) {
                    splitIndex += 50;
                }
                if((splitIndex + split) < write.length()) {
                    split += splitIndex - 1;
                    write.insert(split, "\n");
                } else {
                    break;
                }
            }
        }

        int[] charArray = write.chars().toArray();

        builder.append((char) 0);

        if(! getUtilBoolean("encryptionHead")) {
            builder.append(" encryption: [" + "type=").append(encryptionType.getName()).append(", ");
            builder.append("SUPPORT=MCH -> https://github.com/zhuaidadaya/ConfigUtil , ");
            builder.append("check code=").append(checkingCode).append(", ");
            builder.append("offset=").append(checkingCodeRange).append(", ");
            builder.append("config size=").append(write.length()).append(", ");
            builder.append("config version=").append(getUtilString("version")).append(", ");
            builder.append("split=").append(split).append(", ");
            builder.append("split range=").append(splitRange).append("]");
            builder.append((char) 10);
            builder.append(formatNote());
            builder.append((char) 10);
        } else {
            builder.append(" MCH DB   ");
            write3RandomByte(builder);
            builder.append(" TYPE?").append(encryptionType.getName()).append("  ");
            write3RandomByte(builder);
            builder.append(" SUPPORT?" + "MCH -> https://github.com/zhuaidadaya/ConfigUtil  ");
            write2RandomByte(builder);
            builder.append(" OFFSET?").append(checkingCodeRange);
            write3RandomByte(builder, checkingCodeRange);
            builder.append(" VER?").append(getUtilString("version"));
            write2RandomByte(builder, checkingCodeRange);
            builder.append(" EC?").append(checkingCode);
            write2RandomByte(builder, checkingCodeRange);
            builder.append(" SZ?").append(write.length());
            write3RandomByte(builder, checkingCodeRange);
            builder.append("\n");
        }

        builder.append((char) checkingCodeRange);
        builder.append("\n");
        builder.append((char) checkingCode);

        int count = 0;
        for(Object o : charArray) {
            count++;
            if(Integer.parseInt(o.toString()) == 10) {
                int rand = r.nextInt(checkingCodeRange);
                builder.append((char) 10);
                checkingCode = rand > 13 ? rand : 14;
                if(count != charArray.length)
                    builder.append((char) checkingCode);
            } else {
                builder.append((char) (Integer.parseInt(o.toString()) + checkingCode + checkingCodeRange));
            }
        }

        return builder;
    }

    public StringBuilder encryptionByCompositeSequence(StringBuilder write) {
        Random r = new Random();
        int checkingCodeRange = 1024 * 12;
        int checkingCode = r.nextInt(checkingCodeRange);
        checkingCode = checkingCode > 13 ? checkingCode : 14;
        int[] charArray = write.chars().toArray();

        StringBuilder builder = new StringBuilder();

        builder.append((char) 1);

        if(! getUtilBoolean("encryptionHead")) {
            builder.append(" encryption: [");
            builder.append("type=").append(encryptionType.getName()).append(", ");
            builder.append("SUPPORT=MCH -> https://github.com/zhuaidadaya/ConfigUtil , ");
            builder.append("check code=").append(checkingCode).append(", ");
            builder.append("offset=").append(checkingCodeRange).append(", ");
            builder.append("config size=").append(write.length()).append(", ");
            builder.append("config version=").append(getUtilString("version"));
            builder.append("]");
            builder.append((char) 10);
            builder.append(formatNote());
            builder.append((char) 10);
        } else {
            builder.append(" MCH DB   ");
            write2RandomByte(builder);
            builder.append(" TYPE?").append(encryptionType.getName()).append("  ");
            write3RandomByte(builder);
            builder.append(" SUPPORT?" + "MCH -> https://github.com/zhuaidadaya/ConfigUtil  ");
            write3RandomByte(builder);
            builder.append(" OFFSET?").append(checkingCodeRange);
            write2RandomByte(builder);
            builder.append(" VER?").append(getUtilString("version"));
            write2RandomByte(builder);
            builder.append(" SZ?").append(write.length());
            write3RandomByte(builder);
            builder.append((char) 10);
        }

        builder.append((char) checkingCode);
        builder.append((char) 10);
        builder.append("LIBRARY ");
        write2RandomByte(builder);
        builder.append("  ");
        write3RandomByte(builder);
        builder.append("\n");

        Object2IntMap<String> libraryMap = new Object2IntArrayMap<>();
        Int2IntMap libraryOffsetIndex = new Int2IntArrayMap();

        int count = 0;
        int lim = r.nextInt(checkingCodeRange);
        int head = lim > 13 ? lim : 14;
        int split = 50;
        int libraryLimit = libraryOffset - 1;

        builder.append((char) head);

        int offset;
        int inseparableLevel = getUtilInt("inseparableLevel");

        if(libraryLimit * charArray.length > 10000000) {
            logger.warn(libraryLimit * charArray.length + " sequence building, maybe build a long time");
        }

        //  generate library
        for(Object o : charArray) {
            offset = 0;

            int sourceChar = Integer.parseInt(o.toString());

            try {
                if(libraryOffsetIndex.get(sourceChar) > libraryLimit) {
                    continue;
                }
            } catch (Exception e) {

            }

            boolean dump = false;

            try {
                offset = libraryOffsetIndex.get(sourceChar) + 1;
            } catch (Exception e) {

            }

            count++;

            int writeChar = sourceChar + checkingCode + head;

            while(libraryMap.containsValue(writeChar)) {
                dump = true;
                head++;
                writeChar = sourceChar + checkingCode + head;
            }

            if(dump) {
                builder.append("\b");
                builder.append((char) head);
            }

            if(count > split) {
                builder.append((char) 10);
                switch(inseparableLevel) {
                    case 0 -> split = r.nextInt(15);
                    case 1 -> split = r.nextInt(30);
                    case 2 -> split = r.nextInt(50);
                }
                count = 0;
                lim = r.nextInt(checkingCodeRange);
                head = lim > 13 ? lim : 14;
                builder.append((char) head);
            }

            builder.append((char) writeChar);

            for(Object o2 : o.toString().chars().toArray()) {
                builder.append((char) (Integer.parseInt(o2.toString()) + checkingCode + head));
            }

            if(libraryMap.containsKey(sourceChar + "-0")) {
                libraryOffsetIndex.put(sourceChar, offset);
                libraryMap.put(sourceChar + "-" + offset, writeChar);
            } else {
                libraryOffsetIndex.put(sourceChar, 0);
                libraryMap.put(sourceChar + "-0", writeChar);
            }

            builder.append("\t");
        }

        StringBuilder writeInformation = new StringBuilder();

        for(int c : charArray) {
            if(libraryOffsetIndex.get(c) == 0)
                writeInformation.append((char) libraryMap.get(c + "-0").intValue());
            else
                writeInformation.append((char) libraryMap.get(c + "-" + r.nextInt(libraryOffsetIndex.get(c))).intValue());
        }

        builder.append("\n");
        write2RandomByte(builder);
        builder.append("\n");
        builder.append("INFORMATION ");
        write2RandomByte(builder);
        builder.append("   ");
        write3RandomByte(builder);
        builder.append("\n");

        int tabCount = 0;
        int tab = r.nextInt(15);
        count = 0;
        lim = r.nextInt(checkingCodeRange);
        head = lim > 13 ? lim : 14;
        split = 300;
        builder.append((char) head);
        for(int c : writeInformation.chars().toArray()) {
            count++;
            tabCount++;
            if(count > split) {
                builder.append((char) 10);
                switch(inseparableLevel) {
                    case 0 -> split = r.nextInt(300);
                    case 1 -> split = Math.max(150, r.nextInt(300));
                    case 2 -> split = Math.max(200, r.nextInt(300));
                    case 3 -> split = 300;
                }
                count = 0;
                lim = r.nextInt(checkingCodeRange);
                head = lim > 13 ? lim : 14;
                builder.append((char) head);
            } else if(tabCount > tab & ! (inseparableLevel == 3)) {
                builder.append("\t");
                switch(inseparableLevel) {
                    case 0 -> tab = r.nextInt(15);
                    case 1 -> tab = r.nextInt(30);
                    case 2 -> tab = r.nextInt(50);
                }
                tabCount = 0;
            }

            try {
                builder.append((char) c);
            } catch (Exception e) {

            }
        }

        return builder;
    }

    public void writeRandomByte(StringBuilder writer, int limit, int bytes) {
        Random r = new Random();
        try {
            for(int i = 0; i < bytes; i++) {
                int next = r.nextInt(limit);
                writer.append((char) (next > 13 ? next : 14));
            }
        } catch (Exception e) {

        }
    }

    public void write3RandomByte(StringBuilder writer, int limit) {
        writeRandomByte(writer, limit, 3);
    }

    public void write3RandomByte(StringBuilder writer) {
        write3RandomByte(writer, new Random().nextInt(25565));
    }

    public void write2RandomByte(StringBuilder writer, int limit) {
        writeRandomByte(writer, limit, 2);
    }

    public void write2RandomByte(StringBuilder writer) {
        write2RandomByte(writer, new Random().nextInt(25565));
    }

    public void writeRandomByte(Writer writer, int limit, int bytes) {
        Random r = new Random();
        try {
            for(int i = 0; i < bytes; i++) {
                int next = r.nextInt(limit);
                writer.write(next > 13 ? next : 14);
            }
        } catch (Exception e) {

        }
    }

    public void write3RandomByte(Writer writer, int limit) {
        writeRandomByte(writer, limit, 3);
    }

    public void write3RandomByte(Writer writer) {
        write3RandomByte(writer, new Random().nextInt(25565));
    }

    public void write2RandomByte(Writer writer, int limit) {
        writeRandomByte(writer, limit, 2);
    }

    public void write2RandomByte(Writer writer) {
        write2RandomByte(writer, new Random().nextInt(25565));
    }

    public void remove(Object key) {
        configs.remove(key);
    }

    public void remove(Object key, Object... configValues){
        configs.remove(key, configValues);
    }

    public void set(Object key, Object... configKeysValues) throws IllegalArgumentException {
        setConf(false, key, configKeysValues);
    }

    public void setConf(boolean init, Object key, Object... configKeysValues) throws IllegalArgumentException {
        if(configKeysValues.length > 1) {
            if(configKeysValues.length % 2 != 0)
                throw new IllegalArgumentException("values argument size need Integral multiple of 2, but argument size " + configKeysValues.length + " not Integral multiple of 2");
            configs.put(key, configKeysValues);
        } else {
            configs.put(key, configKeysValues[0]);
        }
        if(getUtilBoolean("autoWrite") & ! init) {
            try {
                writeConfig();
            } catch (Exception e) {

            }
        }
    }

    public void setList(Object key, Object... configValues) {
        setListConf(false, key, configValues);
    }

    public void setListConf(boolean init, Object key, Object... configValues) {
        configs.put(key, configValues);
        if(getUtilBoolean("autoWrite") & ! init) {
            try {
                writeConfig();
            } catch (Exception e) {

            }
        }
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
        for(Object configKey : configs.keySet()) {
            Object config = configs.get(configKey);

            JSONObject conf = new JSONObject();
            JSONObject inJ = new JSONObject();

            if(config instanceof Object[] | config instanceof List<?>) {
                ObjectList<Object> list;
                if(config instanceof Object[]) {
                    list = ObjectList.of((Object[]) config);

                    if(list.size() == 1)
                        list = ObjectList.of(ObjectList.of((Object[]) config).get(0));

                    inJ.put("values", list);
                } else {
                    list = ObjectList.of(config);

                    inJ.put("values", (List<?>) config);
                }

                inJ.put("totalSize", list.size());

                inJ.put("listTag", true);
            } else {
                if(config instanceof String)
                    inJ.put("value", config.toString());
                else if(config instanceof Boolean)
                    inJ.put("value", Boolean.parseBoolean(config.toString()));
                else if(config instanceof Integer)
                    inJ.put("value", Integer.parseInt(config.toString()));
                else
                    inJ.put("value", config);

                inJ.put("listTag", false);
            }

            conf.put(configKey.toString(), inJ);
            addToConfig.put(conf);
        }

        json.put("configs", addToConfig);

        JSONObject manifest = new JSONObject();
        manifest.put("configVersion", getUtilString("version"));
        manifest.put("configsTotal", configs.size());
        manifest.put("encryption", getUtilBoolean("encryption"));
        manifest.put("encryptionHead", getUtilBoolean("encryptionHead"));
        manifest.put("config", new File(getUtilString("path") + "/" + getUtilString("name")));
        manifest.put("autoWrite", getUtilBoolean("autoWrite"));
        json.put("manifest", manifest);

        return json;
    }

    public String formatNote() {
        if(getUtilString("note") != null) {
            try {
                BufferedReader reader = new BufferedReader(new StringReader(getUtilString("note")));
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

    public boolean canShutdown() {
        return canShutdown;
    }

    public void setShuttingDown(boolean shuttingDown) {
        this.shuttingDown = shuttingDown;
    }

    public void shutdown() {
        logger.info("saving configs and shutting down ConfigUtil");
        try {
            while(! canShutdown()) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {

                }
            }

            logger.info("saving configs");

            writeConfig();

            logger.info("all config are saved, shutting down");
        } catch (Exception e) {
            logger.error("failed to save configs, shutting down");
        }
        setShuttingDown(true);
        while(! canShutdown()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {

            }
        }
        shutdown = true;
        logger.info("ConfigUtil are shutdown");
    }

    public int getConfigTotal() {
        return configs.size();
    }

    public boolean isShutdown() {
        return shutdown;
    }

    public String getConfigString(Object config) {
        try {
            return getConfig(config).toString();
        } catch (Exception e) {
            return null;
        }
    }

    public int getConfigInt(Object config) {
        return Integer.parseInt(getConfigString(config));
    }

    public long getConfigLong(Object config) {
        return Long.parseLong(getConfigString(config));
    }

    public boolean getConfigBoolean(Object config) {
        return Boolean.parseBoolean(getConfigString(config));

    }

    public JSONObject getConfigJSONObject(Object config) {
        return new JSONObject(getConfigString(config));

    }

    public JSONArray getConfigJSONArray(Object config) {
        return new JSONArray(getConfigString(config));
    }

    public String getUtilString(Object config) {
        return getConfigString("CU%" + config);
    }

    public boolean getUtilBoolean(Object config) {
        return getConfigBoolean("CU%" + config);
    }

    public int getUtilInt(Object config) {
        return getConfigInt("CU%" + config);
    }

    public long getUtilLong(Object config) {
        return getConfigLong("CU%" + config);
    }

    public JSONObject getUtilJSONObject(Object config) {
        return getConfigJSONObject("CU%" + config);
    }

    public JSONArray getUtilJSONArray(Object config) {
        return getConfigJSONArray("CU%" + config);
    }
}

