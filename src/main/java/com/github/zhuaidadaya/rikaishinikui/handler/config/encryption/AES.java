package com.github.zhuaidadaya.rikaishinikui.handler.config.encryption;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

public class AES {
    public static String base64Encode(byte[] bytes) {
        return Base64.encodeBase64String(bytes);
    }

    public static String aesEncryptToString(byte[] content, byte[] encryptKey) throws Exception {
        return StringUtils.newStringUsAscii(aesEncryptToBytes(content, encryptKey));
    }

    public static byte[] aesEncryptToBytes(byte[] content, byte[] encryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(256);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey, "AES"));

        return cipher.doFinal(content);
    }

    public String randomGet(int size) throws Exception {
        byte[] content = new byte[size];
        byte[] key = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(content);
        random.nextBytes(key);
        byte[] result = aesEncryptToBytes(content, key);

        return base64Encode(result);
    }
}