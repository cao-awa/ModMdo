package com.github.cao.awa.modmdo.utils.file;

import java.io.*;
import java.util.*;
import java.util.zip.*;

public class FileUtil extends MessageDigger {
    public static void deleteFiles(String path) {
        File file = new File(path);
        for (File f : file.listFiles()) {
            if (f.isFile()) {
                f.delete();
            } else {
                deleteFiles(f.getAbsolutePath());
            }
        }
        file.delete();
    }

    public static void unzip(String zip, String path) throws RuntimeException {
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(zip);
            Enumeration<?> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                if (entry.isDirectory()) {
                    String dirPath = path + "/" + entry.getName();
                    File dir = new File(dirPath);
                    dir.mkdirs();
                } else {
                    File targetFile = new File(path + "/" + entry.getName());
                    if (! targetFile.getParentFile().exists()) {
                        targetFile.getParentFile().mkdirs();
                    }
                    targetFile.createNewFile();
                    InputStream is = zipFile.getInputStream(entry);
                    FileOutputStream fos = new FileOutputStream(targetFile);
                    int len;
                    byte[] buf = new byte[4096];
                    while ((len = is.read(buf)) != - 1) {
                        fos.write(buf, 0, len);
                    }
                    fos.close();
                    is.close();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to unzip: " + zip, e);
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String read(BufferedReader reader) {
        String cache;
        StringBuilder builder = new StringBuilder();
        try {
            while ((cache = reader.readLine()) != null)
                builder.append(cache).append("\n");
        } catch (Exception e) {
            return "";
        }
        return builder.toString();
    }

    public static StringBuilder readAsStringBuilder(BufferedReader reader) {
        String cache;
        StringBuilder builder = new StringBuilder();
        try {
            boolean into = false;
            while ((cache = reader.readLine()) != null) {
                builder.append(cache).append(into ? "\n" : "");
                into = true;
            }
        } catch (Exception e) {

        }
        return builder;
    }

    public static void write(File file, StringBuilder information) {
        write(file, information.toString());
    }

    public static void write(File file, String information) {
        try {
            file.getParentFile().mkdirs();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(information);
            writer.close();
        } catch (Exception e) {

        }
    }

    public static void createFile(File f) {
        try {
            createParent(f);
            f.createNewFile();
        } catch (IOException e) {

        }
    }

    public static void createParent(File f) {
        try {
            f.getParentFile().mkdirs();
        } catch (Exception e) {

        }
    }

    public static void insertToHead(File file, String inf) {
        insert(file, 0, inf + "\n");
    }

    public static void insert(File file, int pos, String inf) {
        try {
            if (! (file.exists() && file.isFile())) {
                return;
            }
            if ((pos < 0) || (pos > file.length())) {
                return;
            }

            File f = new File(file.getPath() + ".cache");
            FileOutputStream outputStream = new FileOutputStream(f);
            FileInputStream inputStream = new FileInputStream(f);
            f.deleteOnExit();

            RandomAccessFile rw = new RandomAccessFile(file, "rw");
            rw.seek(pos);

            int cache;
            while ((cache = rw.read()) != - 1) {
                outputStream.write(cache);
            }
            rw.seek(pos);
            rw.write(inf.getBytes());

            while ((cache = inputStream.read()) != - 1) {
                rw.write(cache);
            }
            rw.close();
            outputStream.close();
            inputStream.close();
        } catch (Exception e) {

        }
    }

    public static String strictRead(BufferedReader reader) {
        String cache;
        StringBuilder builder = new StringBuilder();
        try {
            while ((cache = reader.readLine()) != null)
                builder.append(cache).append("\n");
        } catch (Exception e) {
            return "";
        }
        return builder.delete(builder.length() - 1, builder.length()).toString();
    }
}
