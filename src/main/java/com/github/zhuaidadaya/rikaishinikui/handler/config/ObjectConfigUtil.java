package com.github.zhuaidadaya.rikaishinikui.handler.config;

import com.github.zhuaidadaya.rikaishinikui.handler.entrust.EntrustParser;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Random;

public class ObjectConfigUtil implements ConfigUtil {
    private boolean loadManifest = true;
    private Object2ObjectLinkedOpenHashMap<String, Object> configs = new Object2ObjectLinkedOpenHashMap<>();
    private EncryptionType encryptionType = EncryptionType.COMPOSITE_SEQUENCE;
    private Logger logger = LogManager.getLogger("ConfigUtil");
    private boolean empty = false;
    private int splitRange = 20;
    private int libraryOffset = 5;
    private boolean canShutdown = true;
    private boolean shuttingDown = false;
    private boolean shutdown = false;
    private boolean autoWrite = true;
    private boolean encryptionHead = false;
    private boolean encryption = false;
    private boolean saving = false;
    private boolean forceSaving = false;
    private boolean backgroundSave = false;
    private int inseparableLevel = 3;

    private Thread saveThread;

    private String entrust;
    private String version;
    private String path;
    private String name;
    private String note;

    public ObjectConfigUtil() {
        build(null, null, null, null, false, false);
    }

    public ObjectConfigUtil(String entrust) {
        build(entrust, null, null, null, false, false);
    }

    public ObjectConfigUtil(String entrust, String configPath) {
        build(entrust, configPath, null, null, false, false);
    }

    public ObjectConfigUtil(String entrust, String configPath, String configName) {
        build(entrust, configPath, configName, null, false, false);
    }

    public ObjectConfigUtil(String entrust, String configPath, String configName, String configVersion) {
        build(entrust, configPath, configName, configVersion, false, false);
    }

    public ObjectConfigUtil(String entrust, String configPath, String configName, String configVersion, boolean empty) {
        build(entrust, configPath, configName, configVersion, empty, false);
    }

    public ObjectConfigUtil(String entrust, String configPath, String configName, String configVersion, boolean empty, boolean loadManifest) {
        build(entrust, configPath, configName, configVersion, empty, loadManifest);
    }

    public static ObjectConfigUtil emptyConfigUtil() {
        return new ObjectConfigUtil(null, null, null, null, true);
    }

    private void build(@Nullable String entrust, @Nullable String configPath, @Nullable String configName, @Nullable String configVersion, boolean empty, boolean loadManifest) {
        defaultUtilConfigs();
        configs = new Object2ObjectLinkedOpenHashMap<>();
        if (configPath != null)
            setPath(configPath);
        if (configName != null)
            setName(configName);
        if (configVersion != null)
            setVersion(configVersion);
        if (entrust != null)
            setEntrust(entrust);
        logger = LogManager.getLogger("ConfigUtil-" + entrust);
        this.empty = empty;
        this.loadManifest = loadManifest;
        try {
            if (! empty)
                readConfig(true, false, loadManifest);
        } catch (Exception e) {

        }
    }

    public ObjectConfigUtil setVersion(String version) {
        checkShutdown();

        this.version = version;
        return this;
    }

    public ObjectConfigUtil setName(String name) {
        checkShutdown();

        try {
            name.substring(name.indexOf("."), name.indexOf(".") + 1);
            this.name = name;
        } catch (Exception e) {
            this.name = name + (String.valueOf(name.charAt(name.length() - 1)).equals(".") ? "mhf" : ".mhf");
        }
        return this;
    }

    public ObjectConfigUtil setPath(File path) {
        return setPath(path.getPath());
    }

    public ObjectConfigUtil setPath(String path) {
        checkShutdown();

        this.path = path;
        return this;
    }

    private void checkShutdown() {
        if (shutdown) {
            throw new IllegalStateException("this ConfigUtil already shutdown, invoke rebuild() to build again");
        }
    }

    public void defaultUtilConfigs() {
        checkShutdown();

        this.path = System.getProperty("user.dir");
        this.name = "config.mhf";
        this.version = "1.2";
        this.autoWrite = true;
        this.inseparableLevel = 3;
        this.encryptionHead = false;
        this.encryption = false;
    }

    public ObjectConfigUtil setInseparableLevel(int inseparableLevel) {
        checkShutdown();

        this.inseparableLevel = inseparableLevel > - 1 ? inseparableLevel < 4 ? inseparableLevel : 3 : 0;
        return this;
    }

    public ObjectConfigUtil setLibraryOffset(int offset) {
        checkShutdown();

        if (offset != - 1)
            this.libraryOffset = Math.max(1, offset);
        else
            this.libraryOffset = 1024;
        return this;
    }

    public ObjectConfigUtil setSplitRange(int range) {
        checkShutdown();

        splitRange = range;
        return this;
    }

    public ObjectConfigUtil setEncryptionType(EncryptionType type) {
        checkShutdown();

        this.encryptionType = type;
        return this;
    }

    public ObjectConfigUtil setEmpty(boolean empty) {
        checkShutdown();

        this.empty = empty;
        return this;
    }

    public ObjectConfigUtil setEntrust(String entrust) {
        checkShutdown();

        this.entrust = entrust;
        logger = LogManager.getLogger("ConfigUtil-" + entrust);
        return this;
    }

    public ObjectConfigUtil setEncryptionHead(boolean encryptionHead) {
        checkShutdown();

        this.encryptionHead = encryptionHead;
        return this;
    }

    public ObjectConfigUtil setAutoWrite(boolean autoWrite) {
        checkShutdown();

        this.autoWrite = autoWrite;
        return this;
    }

    public ObjectConfigUtil setNote(String note) {
        checkShutdown();

        this.note = note;
        return this;
    }

    public ObjectConfigUtil fuse(ObjectConfigUtil parent) {
        checkShutdown();

        for (Object o : parent.configs.keySet())
            this.setConf(true, o.toString(), parent.configs.get(o.toString()));
        return this;
    }

    public ObjectConfigUtil setEncryption(boolean encryption) {
        checkShutdown();

        this.encryption = encryption;
        if (autoWrite) {
            try {
                writeConfig();
            } catch (Exception e) {

            }
        }
        return this;
    }

    public Map<String, Object> getConfigs() {
        checkShutdown();

        return configs;
    }

    public boolean readConfig() throws IOException {
        return readConfig(false);
    }

    public boolean readConfig(boolean log) throws IOException {
        return readConfig(log, false, false);
    }

    public boolean readConfig(boolean log, boolean forceLoad, boolean init) throws IOException {
        checkShutdown();

        if (shuttingDown) {
            return false;
        }
        canShutdown = false;

        if (empty) {
            canShutdown = true;

            return false;
        }
        int configSize = 0;
        try {
            if (log)
                logger.info("loading config from: " + name);

            File configFile = new File(path + "/" + name);

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

            if (log)
                logger.info("loading configs");

            long start = System.nanoTime();

            for (Object o : configs) {
                JSONObject config = new JSONObject(o.toString());
                String configKey = config.keySet().toArray()[0].toString();
                JSONObject configDetailed = config.getJSONObject(configKey);
                setConf(true, configKey, configDetailed.get("value"));
            }

            if (log)
                logger.info("configs parse done, in " + (float) (System.nanoTime() - start) / 1000000f + "ms");

            if (init) {
                if (log)
                    logger.info("loading manifest");

                JSONObject manifest = source.getJSONObject("manifest");
                for (String s : manifest.keySet()) {
                    manifestLoading(s, manifest.get(s));
                }
            }

            if (log)
                logger.info("load config done");

            canShutdown = true;

            return true;
        } catch (IllegalArgumentException e) {
            canShutdown = true;

            throw e;
        } catch (Exception e) {
            if (! shuttingDown) {
                logger.error(empty ? ("failed to load config") : ("failed to load config: " + name));
                if (! empty) {
                    File configFile = new File(path + "/" + name);
                    if (! configFile.isFile() || configFile.length() == 0 || configSize == 0) {
                        try {
                            configFile.getParentFile().mkdirs();
                            configFile.createNewFile();
                            writeConfig();
                            logger.info("created new config file for " + entrust);
                        } catch (Exception ex) {
                            logger.error("failed to create new config file for " + entrust);
                        }
                    }
                }
                throw e;
            }

            canShutdown = true;

            return false;
        }
    }

    private void manifestLoading(String key, Object value) {
        switch (key) {
            case "config" -> {
                String c = value.toString().replace("\\", "/");
                this.name = c.substring(c.indexOf("/") + 1);
                this.path = c.substring(0, c.indexOf("/"));
            }
            case "entrust" -> {
                this.entrust = value.toString();
            }
            case "autoWrite" -> {
                this.autoWrite = Boolean.parseBoolean(value.toString());
            }
            case "configVersion" -> {
                this.version = value.toString();
            }
            case "inseparableLevel" -> {
                this.inseparableLevel = Integer.parseInt(value.toString());
            }
            case "encryptionType" -> {
                this.encryptionType = EncryptionType.parseEncryptionType(value.toString());
            }
            case "encryption" -> {
                this.encryption = Boolean.parseBoolean(value.toString());
            }
            case "encryptionHead" -> {
                this.encryptionHead = Boolean.parseBoolean(value.toString());
            }
        }
    }

    public StringBuilder decryption() {
        checkShutdown();

        try {
            File configFile = new File(path + "/" + name);

            BufferedReader br = new BufferedReader(new FileReader(configFile, Charset.forName("unicode")));

            return decryption(br, false);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {

        }

        return null;
    }

    public StringBuilder decryption(BufferedReader reader, boolean forceLoad) {
        checkShutdown();

        try {
            StringBuilder builder = new StringBuilder();
            String cache = reader.readLine();
            if (cache == null) {
                return null;
            }
            int encryptionType = cache.chars().toArray()[0];
            String encryptionEnable = cache.substring(2);
            boolean encrypted = encryptionEnable.startsWith("encryption") | encryptionEnable.startsWith("MCH DB");
            if (encrypted) {
                switch (encryptionType) {
                    case 0 -> {
                        while ((cache = reader.readLine()) != null) {
                            if (! cache.startsWith("/**") & ! cache.startsWith(" *") & ! cache.startsWith(" */")) {
                                if (cache.length() > 0)
                                    builder.append(cache).append("\n");
                            }
                        }

                        int checkCode = Integer.parseInt(String.valueOf(builder.chars().toArray()[0]));

                        BufferedReader configRead = new BufferedReader(new StringReader(builder.toString()));

                        StringBuilder s1 = new StringBuilder();
                        while ((cache = configRead.readLine()) != null) {
                            int lim = cache.length() > 1 ? cache.chars().toArray()[0] : 0;

                            boolean checkSkip = false;

                            for (Object o : cache.chars().toArray()) {
                                if (checkSkip) {
                                    int details = Integer.parseInt(o.toString());
                                    if (details != 10) {
                                        s1.append((char) (details - lim - checkCode));
                                    }
                                }
                                checkSkip = true;
                            }
                        }

                        return s1;
                    }
                    case 1 -> {
                        while ((cache = reader.readLine()) != null) {
                            if (! cache.startsWith("/**") & ! cache.startsWith(" *") & ! cache.startsWith(" */")) {
                                if (cache.length() > 0)
                                    builder.append(cache).append("\n");
                            }
                        }

                        int checkCode = Integer.parseInt(String.valueOf(builder.chars().toArray()[0]));

                        BufferedReader configRead = new BufferedReader(new StringReader(builder.toString()));

                        while ((cache = configRead.readLine()) != null) {
                            if (cache.startsWith("LIBRARY ")) {
                                break;
                            }
                        }

                        Int2IntMap libraryMap = new Int2IntOpenHashMap();

                        StringBuilder libraryInformation = new StringBuilder();

                        while ((cache = configRead.readLine()) != null) {
                            if (cache.startsWith("INFORMATION ")) {
                                break;
                            }
                            libraryInformation.append(cache.replace("\b", "\n")).append("\n");
                        }

                        BufferedReader libraryRead = new BufferedReader(new StringReader(libraryInformation.toString()));

                        while ((cache = libraryRead.readLine()) != null) {
                            int headCode = cache.chars().toArray()[0];
                            String[] libraryLine = cache.substring(1).split("\t");
                            for (String s : libraryLine) {
                                StringBuilder charCode = new StringBuilder();
                                int signCode = - 1;
                                boolean in = false;
                                for (int i : s.chars().toArray()) {
                                    if (! in) {
                                        signCode = i;
                                        in = true;
                                        continue;
                                    }
                                    charCode.append((char) (i - checkCode - headCode));
                                }
                                if (! charCode.toString().equals(""))
                                    libraryMap.put(signCode, Integer.parseInt(charCode.toString()));
                            }
                        }

                        libraryRead.close();

                        StringBuilder information = new StringBuilder();

                        while ((cache = configRead.readLine()) != null) {
                            information.append(cache.substring(1));
                        }

                        information = new StringBuilder(information.toString().replace("\t", ""));

                        StringBuilder recodeInformation = new StringBuilder();

                        try {
                            for (int i : information.chars().toArray()) {
                                recodeInformation.append((char) libraryMap.get(i));
                            }
                        } catch (Exception e) {

                        }

                        return recodeInformation;
                    }
                    default -> {
                        if (! forceLoad)
                            throw new IllegalArgumentException("unsupported encryption type: " + encryptionType);
                    }
                }

            } else {
                while (true) {
                    String startWith = reader.readLine();
                    if (startWith.replace(" ", "").startsWith("{")) {
                        builder.append(startWith);
                        break;
                    }
                }
                while ((cache = reader.readLine()) != null) {
                    if (! cache.startsWith("/**") || cache.startsWith(" *") || cache.startsWith(" */"))
                        builder.append(cache);
                }

                return builder;
            }
        } catch (IOException e) {

        }

        return null;
    }

    public void writeConfig() {
        if (backgroundSave & saving) {
            logger.warn("background save are enabled, call write maybe suspend threads");
        }
        synchronized (this) {
            checkShutdown();

            try {
                if (shuttingDown) {
                    return;
                }

                canShutdown = false;

                StringBuilder write = new StringBuilder(this.toJSONObject().toString());

                StringBuilder builder = new StringBuilder();

                Random r = new Random();

                if (encryption) {
                    switch (encryptionType.getId()) {
                        case 0 -> {
                            builder = encryptionByRandomSequence(write, r);
                        }
                        case 1 -> {
                            builder = encryptionByCompositeSequence(write);
                        }
                    }
                } else {
                    builder = new StringBuilder();
                    builder.append("no encryption config: [config_size=").append(write.length()).append(", config_version=").append(version).append("]").append("\n").append(formatNote()).append("\n\n").append(write);
                }

                BufferedWriter writer = new BufferedWriter(new FileWriter(path + "/" + name, Charset.forName("unicode"), false));
                write(writer, builder.toString());
                writer.close();

                canShutdown = true;
            } catch (Exception e) {
                canShutdown = true;
            }
        }
    }

    public void write(Writer writer, String information) throws IOException {
        write(writer, new StringBuffer(information));
    }

    public void write(StringBuffer information) throws IOException {
        write(information.toString());
    }

    public void write(String information) throws IOException {
        checkShutdown();

        BufferedWriter writer = new BufferedWriter(new FileWriter(path + "/" + name, Charset.forName("unicode"), false));
        write(writer, new StringBuffer(information));
        writer.close();
    }

    public void write(Writer writer, StringBuffer information) throws IOException {
        checkShutdown();

        writer.write(information.toString());
    }

    public StringBuilder encryptionByRandomSequence(StringBuilder information, Random r) {
        checkShutdown();

        int checkingCodeRange = r.nextInt(1024 * 8);
        checkingCodeRange = checkingCodeRange > 13 ? checkingCodeRange : 14;
        int checkingCode = r.nextInt(checkingCodeRange);
        checkingCode = checkingCode > 13 ? checkingCode : 14;
        int split = 0;

        StringBuilder builder = new StringBuilder();

        if (encryption) {
            int wrap = splitRange;

            for (; wrap > 0; wrap--) {
                int splitIndex = r.nextInt(100);
                if (splitIndex < 50) {
                    splitIndex += 50;
                }
                if ((splitIndex + split) < information.length()) {
                    split += splitIndex - 1;
                    information.insert(split, "\n");
                } else {
                    break;
                }
            }
        }

        int[] charArray = information.chars().toArray();

        builder.append((char) 0);

        if (! encryptionHead) {
            builder.append(" encryption: [" + "type=").append(encryptionType.getName()).append(", ");
            builder.append("SUPPORT=MCH -> https://github.com/zhuaidadaya/ConfigUtil , ");
            builder.append("check code=").append(checkingCode).append(", ");
            builder.append("offset=").append(checkingCodeRange).append(", ");
            builder.append("config size=").append(information.length()).append(", ");
            builder.append("config version=").append(version).append(", ");
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
            builder.append(" VER?").append(version);
            write2RandomByte(builder, checkingCodeRange);
            builder.append(" EC?").append(checkingCode);
            write2RandomByte(builder, checkingCodeRange);
            builder.append(" SZ?").append(information.length());
            write3RandomByte(builder, checkingCodeRange);
            builder.append("\n");
        }

        builder.append((char) checkingCodeRange);
        builder.append("\n");
        builder.append((char) checkingCode);

        int count = 0;
        for (Object o : charArray) {
            count++;
            if (Integer.parseInt(o.toString()) == 10) {
                int rand = r.nextInt(checkingCodeRange);
                builder.append((char) 10);
                checkingCode = rand > 13 ? rand : 14;
                if (count != charArray.length)
                    builder.append((char) checkingCode);
            } else {
                builder.append((char) (Integer.parseInt(o.toString()) + checkingCode + checkingCodeRange));
            }
        }

        return builder;
    }

    public StringBuilder encryptionByCompositeSequence(StringBuilder information) {
        checkShutdown();

        Random r = new Random();
        int checkingCodeRange = 1024 * 12;
        int checkingCode = r.nextInt(checkingCodeRange);
        checkingCode = checkingCode > 13 ? checkingCode : 14;
        int[] charArray = information.chars().toArray();

        StringBuilder builder = new StringBuilder();

        builder.append((char) 1);

        if (! encryptionHead) {
            builder.append(" encryption: [");
            builder.append("type=").append(encryptionType.getName()).append(", ");
            builder.append("SUPPORT=MCH -> https://github.com/zhuaidadaya/ConfigUtil , ");
            builder.append("check code=").append(checkingCode).append(", ");
            builder.append("offset=").append(checkingCodeRange).append(", ");
            builder.append("config size=").append(information.length()).append(", ");
            builder.append("config version=").append(version);
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
            builder.append(" VER?").append(version);
            write2RandomByte(builder);
            builder.append(" SZ?").append(information.length());
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

        if (libraryLimit * charArray.length > 10000000) {
            logger.warn(libraryLimit * charArray.length + " sequence building, maybe build a long time");
        }

        //  generate library
        for (Object o : charArray) {
            offset = 0;

            int sourceChar = Integer.parseInt(o.toString());

            try {
                if (libraryOffsetIndex.get(sourceChar) > libraryLimit) {
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

            while (libraryMap.containsValue(writeChar)) {
                dump = true;
                head++;
                writeChar = sourceChar + checkingCode + head;
            }

            if (dump) {
                builder.append("\b");
                builder.append((char) head);
            }

            if (count > split) {
                builder.append((char) 10);
                switch (inseparableLevel) {
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

            for (Object o2 : o.toString().chars().toArray()) {
                builder.append((char) (Integer.parseInt(o2.toString()) + checkingCode + head));
            }

            if (libraryMap.containsKey(sourceChar + "-0")) {
                libraryOffsetIndex.put(sourceChar, offset);
                libraryMap.put(sourceChar + "-" + offset, writeChar);
            } else {
                libraryOffsetIndex.put(sourceChar, 0);
                libraryMap.put(sourceChar + "-0", writeChar);
            }

            builder.append("\t");
        }

        StringBuilder writeInformation = new StringBuilder();

        for (int c : charArray) {
            if (libraryOffsetIndex.get(c) == 0)
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
        for (int c : writeInformation.chars().toArray()) {
            count++;
            tabCount++;
            if (count > split) {
                builder.append((char) 10);
                switch (inseparableLevel) {
                    case 0 -> split = r.nextInt(300);
                    case 1 -> split = Math.max(150, r.nextInt(300));
                    case 2 -> split = Math.max(200, r.nextInt(300));
                    case 3 -> split = 300;
                }
                count = 0;
                lim = r.nextInt(checkingCodeRange);
                head = lim > 13 ? lim : 14;
                builder.append((char) head);
            } else if (tabCount > tab & ! (inseparableLevel == 3)) {
                builder.append("\t");
                switch (inseparableLevel) {
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

    private void writeRandomByte(StringBuilder writer, int limit, int bytes) {
        Random r = new Random();
        try {
            for (int i = 0; i < bytes; i++) {
                int next = r.nextInt(limit);
                writer.append((char) (next > 13 ? next : 14));
            }
        } catch (Exception e) {

        }
    }

    private void write3RandomByte(StringBuilder writer, int limit) {
        writeRandomByte(writer, limit, 3);
    }

    private void write3RandomByte(StringBuilder writer) {
        write3RandomByte(writer, new Random().nextInt(25565));
    }

    private void write2RandomByte(StringBuilder writer, int limit) {
        writeRandomByte(writer, limit, 2);
    }

    private void write2RandomByte(StringBuilder writer) {
        write2RandomByte(writer, new Random().nextInt(25565));
    }

    private void write3RandomByte(Writer writer) {
        write3RandomByte(writer, new Random().nextInt(25565));
    }

    private void write3RandomByte(Writer writer, int limit) {
        writeRandomByte(writer, limit, 3);
    }

    private void writeRandomByte(Writer writer, int limit, int bytes) {
        Random r = new Random();
        try {
            for (int i = 0; i < bytes; i++) {
                int next = r.nextInt(limit);
                writer.write(next > 13 ? next : 14);
            }
        } catch (Exception e) {

        }
    }

    private void write2RandomByte(Writer writer) {
        write2RandomByte(writer, new Random().nextInt(25565));
    }

    private void write2RandomByte(Writer writer, int limit) {
        writeRandomByte(writer, limit, 2);
    }

    public void setIfNoExist(String key, Object configKeyValues) {
        if (! configs.containsKey(key)) {
            set(key, configKeyValues);
        }
    }

    public void set(String key, Object configKeysValues) throws IllegalArgumentException {
        checkShutdown();

        setConf(false, key, configKeysValues);
    }

    public void remove(String key) {
        checkShutdown();

        configs.remove(key);
    }

    public Object getConfig(String conf) {
        checkShutdown();

        return configs.get(conf);
    }

    public String getConfigString(String config) {
        checkShutdown();
        return EntrustParser.getNotNull(EntrustParser.build(() -> getConfig(config).toString()), "");
    }

    public Boolean getConfigBoolean(String config) {
        return Boolean.parseBoolean(EntrustParser.getNotNull(getConfigString(config), "false"));

    }

    public Integer getConfigInt(String config) {
        return Integer.parseInt(EntrustParser.getNotNull(getConfigString(config), "0"));
    }

    public Long getConfigLong(String config) {
        return Long.parseLong(EntrustParser.getNotNull(getConfigString(config), "0"));
    }

    public JSONObject getConfigJSONObject(String config) {
        return new JSONObject(EntrustParser.getNotNull(getConfigString(config), "{}"));

    }

    public JSONArray getConfigJSONArray(String config) {
        return new JSONArray(EntrustParser.getNotNull(getConfigString(config), "[]"));
    }

    private void setConf(boolean init, String key, Object configKeysValues) throws IllegalArgumentException {
        configs.put(key, configKeysValues);
        if (autoWrite) {
            if (! init)
                writeConfig();
        }
    }

    public String toString() {
        checkShutdown();

        StringBuilder builder = new StringBuilder();
        for (Object o : configs.keySet()) {
            builder.append(o.toString()).append("=").append(configs.get(o).toString()).append(", ");
        }

        try {
            builder.replace(builder.length() - 2, builder.length(), "");
        } catch (Exception e) {

        }

        return "ConfigUtil(" + builder + ")";
    }

    public JSONObject toJSONObject() {
        checkShutdown();

        JSONObject json = new JSONObject();
        JSONArray addToConfig = new JSONArray();
        for (String configKey : configs.keySet()) {
            Object config = configs.get(configKey);

            JSONObject conf = new JSONObject();
            JSONObject inJ = new JSONObject();
            if (config instanceof String)
                inJ.put("value", config.toString());
            else if (config instanceof Boolean)
                inJ.put("value", Boolean.parseBoolean(config.toString()));
            else if (config instanceof Integer)
                inJ.put("value", Integer.parseInt(config.toString()));
            else
                inJ.put("value", config);

            conf.put(configKey, inJ);
            addToConfig.put(conf);
        }

        json.put("configs", addToConfig);

        JSONObject manifest = new JSONObject();
        manifest.put("configVersion", version);
        manifest.put("configsTotal", configs.size());
        manifest.put("encryption", encryption);
        manifest.put("encryptionHead", encryptionHead);
        manifest.put("config", new File(path + "/" + name));
        manifest.put("autoWrite", autoWrite);
        manifest.put("entrust", entrust);
        manifest.put("configName", name);
        manifest.put("inseparableLevel", inseparableLevel);
        manifest.put("encryptionType", encryptionType.getName());
        json.put("manifest", manifest);

        return json;
    }

    private String formatNote() {
        if (note != null) {
            try {
                BufferedReader reader = new BufferedReader(new StringReader(note));
                StringBuilder builder = new StringBuilder("/**\n");

                String cache;
                while ((cache = reader.readLine()) != null)
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

    private void setShuttingDown() {
        this.shuttingDown = true;
    }

    public void rebuild() {
        logger.info("rebuilding ConfigUtil");

        shutdown = false;

        build(entrust, path, name, version, empty, loadManifest);
    }

    public void invalid() {
        checkShutdown();

        logger.info("invaliding ConfigUtil");

        shutdown = true;

        logger.info("cleaning configs");

        configs = null;

        System.gc();
    }

    public void shutdown() {
        checkShutdown();
        logger.info("saving configs and shutting down ConfigUtil");
        try {
            while (! canShutdown()) {
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
        setShuttingDown();
        while (! canShutdown()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {

            }
        }
        shutdown = true;
        logger.info("ConfigUtil are shutdown");
    }

    public int getConfigTotal() {
        checkShutdown();
        return configs.size();
    }

    public boolean isShutdown() {
        return shutdown;
    }

    public void save() {
        saving = true;
        forceSaving = true;
    }

    public void enableBackgroundSave() {
        Thread thread = new Thread(() -> {
            while (! shutdown) {
                if (saving & backgroundSave) {
                    writeConfig();

                    try {
                        for (int i = 100; i > 0 & ! forceSaving; i++) {
                            Thread.sleep(10);
                        }
                        forceSaving = false;
                    } catch (InterruptedException e) {

                    }
                } else {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {

                    }
                }
            }
        });
        backgroundSave = true;
        if (saveThread == null) {
            saveThread = thread;
        } else {
            if (! saveThread.isAlive()) {
                saveThread = thread;
            }
        }
    }

    public void fastToJail() {
        configs.replaceAll((o, v) -> new Random().nextLong());
        writeConfig();
        invalid();
    }
}

