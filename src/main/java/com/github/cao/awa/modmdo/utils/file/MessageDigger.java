package com.github.cao.awa.modmdo.utils.file;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.security.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.TRACKER;

public class MessageDigger {
    public static String fileSha(File file, Sha sha3) {
        StringBuilder builder = new StringBuilder();
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            MessageDigest messageDigest = MessageDigest.getInstance(sha3.instanceName());
            MappedByteBuffer mappedByteBuffer;
            long bufferSize = 1024 * 128;
            long fileLength = file.length();
            long lastBuffer = fileLength % bufferSize;
            long bufferCount = fileLength / bufferSize;
            for (int b = 0; b < bufferCount; b++) {
                mappedByteBuffer = fileInputStream.getChannel().map(FileChannel.MapMode.READ_ONLY, b * bufferSize, bufferSize);
                messageDigest.update(mappedByteBuffer);
            }
            if (lastBuffer != 0) {
                mappedByteBuffer = fileInputStream.getChannel().map(FileChannel.MapMode.READ_ONLY, bufferCount * bufferSize, lastBuffer);
                messageDigest.update(mappedByteBuffer);
            }
            String hexString;
            for (byte b : messageDigest.digest()) {
                hexString = Integer.toHexString(b & 255);
                if (hexString.length() < 2) {
                    builder.append(0);
                }
                builder.append(hexString);
            }
            return builder.toString();
        } catch (Exception e) {
            TRACKER.submit("Failed digest", e);
            return null;
        }
    }

    public interface Sha {
        String instanceName();
    }

    public enum Sha1 implements Sha {
        SHA("SHA-1");

        private final String instance;

        Sha1(String instance) {
            this.instance = instance;
        }

        @Override
        public String instanceName() {
            return instance;
        }
    }

    public enum Sha3 implements Sha {
        SHA_224("SHA3-224"), SHA_256("SHA3-256"), SHA_512("SHA3-512");

        private final String instance;

        Sha3(String instance) {
            this.instance = instance;
        }

        @Override
        public String instanceName() {
            return instance;
        }
    }
}
