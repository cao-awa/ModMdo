package com.github.cao.awa.modmdo.utils.file.reads;

import java.io.*;

public class FileReads {
    public static String read(BufferedReader reader) {
        String cache;
        StringBuilder builder = new StringBuilder();
        try {
            while ((cache = reader.readLine()) != null) {
                builder.append(cache).append("\n");
            }
            reader.close();
        } catch (Exception e) {
            return "";
        }
        return builder.toString();
    }

    public static String strictRead(BufferedInputStream reader) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            byte[] buf = new byte[4096];
            int size;
            while ((size = reader.read(buf)) > - 1) {
                out.write(buf, 0, size);
            }
        } catch (Exception e) {
            return "";
        }
        return out.toString();
    }
}
