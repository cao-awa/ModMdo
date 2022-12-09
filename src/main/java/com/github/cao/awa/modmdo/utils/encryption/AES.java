package com.github.cao.awa.modmdo.utils.encryption;

import org.apache.commons.codec.binary.*;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;

public class AES {
    private static final byte[] KEY_VI = "0000000000000000".getBytes();

    static {
        Security.setProperty("crypto.policy", "unlimited");
    }

    public static String aesEncryptToString(byte[] content, byte[] key) throws Exception {
        return StringUtils.newStringUtf8(aesEncrypt(content, key));
    }

    public static String aesDecryptToString(byte[] content, byte[] key) throws Exception {
        return StringUtils.newStringUtf8(aesDecrypt(content, key));
    }

    public static byte[] aesDecrypt(byte[] content, byte[] key) throws Exception {
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(KEY_VI));
        return cipher.doFinal(Base64.decodeBase64(content));
    }

    public static byte[] aesEncrypt(byte[] content, byte[] key) throws Exception {
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(KEY_VI));
        return Base64.encodeBase64(cipher.doFinal(content));
    }

    public static String base64Encode(byte[] bytes) {
        return Base64.encodeBase64String(bytes);
    }
}
