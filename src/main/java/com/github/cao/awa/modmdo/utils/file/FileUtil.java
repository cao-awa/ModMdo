package com.github.cao.awa.modmdo.utils.file;

import java.io.*;

public class FileUtil {
    public static void mkdirs(File file) {
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static void mkdirsParent(File file) {
        file = file.getParentFile();
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static void delete(File file) {
        if (file.exists()) {
            file.delete();
        }
    }
}
