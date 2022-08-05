package com.github.zhuaidadaya.rikaishinikui.handler.config;

import com.github.cao.awa.modmdo.utils.file.*;
import com.github.cao.awa.modmdo.utils.file.reads.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import org.json.*;

import java.io.*;
import java.nio.charset.*;
import java.util.zip.*;

public record DiskObjectConfigUtil(String entrust, String path, String suffix, boolean compress) {
    public void setIfNoExist(String key, Object value) {
        if (new File(getConfigPath(key)).isFile()) {
            return;
        }
        set(key, value == null ? "null" : value.toString());
    }

    public void set(String key, Object value) {
        FileUtil.write(new File(getConfigPath(key)), compress ? compress(value.toString(), Deflater.DEFAULT_STRATEGY) : value.toString());
    }

    public static String compress(String str, int strategy) {
        if (str == null || str.length() == 0) {
            return str;
        }
        if (strategy == - 1) {
            strategy = Deflater.DEFAULT_STRATEGY;
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
            deflater.setStrategy(strategy);
            DeflaterOutputStream gzip = new DeflaterOutputStream(out, deflater);
            gzip.write(str.getBytes(StandardCharsets.UTF_8));
            gzip.close();
            return out.toString(StandardCharsets.ISO_8859_1);
        } catch (Exception e) {
            return str;
        }
    }

    public String getConfigPath(String key) {
        return path + "/" + key + "." + suffix;
    }

    public void set(String key, Object value, int strategy) {
        if (compress) {
            FileUtil.write(new File(getConfigPath(key)), compress(value.toString(), strategy));
        } else {
            FileUtil.write(new File(getConfigPath(key)), value.toString());
        }
    }

    public String getConfigString(String key) {
        return get(key);
    }

    public String get(String key) {
        return EntrustParser.trying(() -> decompress(FileReads.strictRead(new BufferedInputStream(new FileInputStream(getConfigPath(key))))));
    }

    public static String decompress(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            InflaterInputStream gunzip = new InflaterInputStream(new ByteArrayInputStream(str.getBytes(StandardCharsets.ISO_8859_1)));
            byte[] buf = new byte[4096];
            int size;
            while ((size = gunzip.read(buf)) > - 1) {
                out.write(buf, 0, size);
            }
            return out.toString(StandardCharsets.UTF_8);
        } catch (Exception ex) {
            return str;
        }
    }

    public JSONObject getConfigJSONObject(String key) {
        return EntrustParser.trying(() -> new JSONObject(get(key)), () -> new JSONObject());
    }

    public boolean getConfigBoolean(String key) {
        return "true".equalsIgnoreCase(get(key));
    }

    public int getConfigInt(String key) {
        return EntrustParser.trying(() -> Integer.parseInt(get(key)), () -> - 1);
    }

    public int getConfigInt(String key, int defaultValue) {
        return EntrustParser.trying(() -> Integer.parseInt(get(key)), () -> defaultValue);
    }

    public long getConfigLong(String key) {
        return EntrustParser.trying(() -> Long.parseLong(get(key)), () -> - 1L);
    }

    public long getConfigLong(String key, long defaultValue) {
        return EntrustParser.trying(() -> Long.parseLong(get(key)), () -> defaultValue);
    }

    public boolean getConfigBoolean(String key, boolean defaultValue) {
        return EntrustParser.trying(() -> Boolean.parseBoolean(get(key)), () -> defaultValue);
    }
}