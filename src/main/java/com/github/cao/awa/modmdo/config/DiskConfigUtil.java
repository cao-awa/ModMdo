package com.github.cao.awa.modmdo.config;

import com.github.cao.awa.modmdo.information.compressor.deflater.*;
import com.github.cao.awa.modmdo.utils.io.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import org.apache.commons.codec.binary.*;
import org.json.*;

import java.io.*;
import java.nio.charset.*;

public final class DiskConfigUtil {
    private final String entrust;
    private final String path;
    private final String suffix;
    private final boolean compress;

    public DiskConfigUtil(String entrust, String path, String suffix, boolean compress) {
        this.entrust = entrust;
        this.path = path;
        this.suffix = suffix;
        this.compress = compress;

        new File(path).mkdirs();
    }

    public void set(String key, Object value) {
        EntrustEnvironment.trys(() -> IOUtil.write(
                new FileOutputStream(getConfigPath(key)),
                compress ?
                compress(value.toString()
                              .getBytes(StandardCharsets.UTF_8)) :
                value.toString()
                     .getBytes()
        ));
    }

    public static byte[] compress(byte[] str) {
        if (str == null || str.length == 0) {
            return DeflaterCompressor.EMPTY_BYTES;
        }
        return DeflaterCompressor.INSTANCE.compress(str);
    }

    public String getConfigPath(String key) {
        return path + "/" + key + "." + suffix;
    }

    public String getString(String key) {
        return EntrustEnvironment.trys(() -> StringUtils.newStringUtf8(decompress(IOUtil.readBytes(new BufferedInputStream(new FileInputStream(getConfigPath(key)))))));
    }

    public static byte[] decompress(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return DeflaterCompressor.EMPTY_BYTES;
        }
        return DeflaterCompressor.INSTANCE.decompress(bytes);
    }

    public void setIfNoExist(String key, Object value) {
        if (new File(getConfigPath(key)).isFile()) {
            return;
        }
        set(
                key,
                value == null ? "null" : value.toString()
        );
    }

    public JSONObject getJSONObject(String key) {
        return EntrustEnvironment.trys(
                () -> new JSONObject(getString(key)),
                () -> new JSONObject()
        );
    }

    public int getInt(String key) {
        return getInt(
                key,
                - 1
        );
    }

    public int getInt(String key, int defaultValue) {
        return EntrustEnvironment.trys(
                () -> Integer.parseInt(getString(key)),
                () -> defaultValue
        );
    }

    public long getLong(String key) {
        return getLong(
                key,
                - 1L
        );
    }

    public long getLong(String key, long defaultValue) {
        return EntrustEnvironment.trys(
                () -> Long.parseLong(getString(key)),
                () -> defaultValue
        );
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return EntrustEnvironment.trys(
                () -> getBoolean(key),
                () -> defaultValue
        );
    }

    public boolean getBoolean(String key) {
        return "true".equalsIgnoreCase(getString(key));
    }
}