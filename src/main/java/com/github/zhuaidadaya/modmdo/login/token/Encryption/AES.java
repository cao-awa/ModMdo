package com.github.zhuaidadaya.modmdo.login.token.Encryption;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

public class AES {
    private byte[] key;

    public static String base64Encode(byte[] bytes) {
        return Base64.encodeBase64String(bytes);
    }

    public static byte[] aesEncryptToBytes(byte[] content, byte[] encryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(256);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey, "AES"));

        return cipher.doFinal(content);
    }

    public byte[] getKey() {
        return key;
    }

    public String randomGet(int size) throws Exception {
        byte[] content = new byte[size];
        byte[] key = new byte[16];
        this.key = key;
        SecureRandom random = new SecureRandom();
        random.nextBytes(content);
        random.nextBytes(key);
        byte[] result = aesEncryptToBytes(content, key);

        return base64Encode(result);
    }
}