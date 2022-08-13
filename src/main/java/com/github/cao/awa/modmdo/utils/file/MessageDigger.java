package com.github.cao.awa.modmdo.utils.file;

import java.io.*;
import java.security.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

public class MessageDigger {
    public static String fileSha(File file, Sha sha) {
        try {
            int buff = 16384;

            RandomAccessFile accessFile = new RandomAccessFile(file, "r");

            MessageDigest messageDigest = MessageDigest.getInstance(sha.instanceName());

            byte[] buffer = new byte[buff];

            long read = 0;

            long offset = accessFile.length();
            int unitsize;
            while (read < offset) {
                unitsize = (int) (((offset - read) < buff) ? (offset - read) : buff);
                accessFile.read(buffer, 0, unitsize);

                messageDigest.update(buffer, 0, unitsize);

                read += unitsize;
            }

            accessFile.close();

            StringBuilder builder = new StringBuilder();

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
            return "null";
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
