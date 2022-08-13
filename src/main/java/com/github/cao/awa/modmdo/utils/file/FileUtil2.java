package com.github.cao.awa.modmdo.utils.file;

import it.unimi.dsi.fastutil.io.*;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.*;
import java.util.zip.*;

public class FileUtil2 {
    public static void deleteFiles(String path) {
        File file = new File(path);
        for (File f : file.listFiles()) {
            if (f.isFile()) {
                deleteFile(f);
            } else {
                deleteFiles(f.getAbsolutePath());
            }
        }
        deleteFile(file);
    }

    public static boolean deleteFile(String path) {
        return deleteFile(new File(path));
    }

    public static boolean deleteFile(File file) {
        return file.delete();
    }

    public static void createParent(String f) {
        createParent(new File(f));
    }

    public static void createParent(File f) {
        try {
            f.getParentFile().mkdirs();
        } catch (Exception e) {

        }
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
                    createFile(targetFile.getAbsolutePath());
                    FastBufferedInputStream is = new FastBufferedInputStream(zipFile.getInputStream(entry));
                    FastBufferedOutputStream fos = new FastBufferedOutputStream(new FileOutputStream(targetFile));
                    int len;
                    byte[] buf = new byte[1024 * 1024];
                    while ((len = is.read(buf)) != - 1) {
                        fos.write(buf, 0, len);
                    }
                    fos.close();
                    is.close();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("failed to unzip: " + zip, e);
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

    public static void createFile(String f) {
        createFile(new File(f));
    }

    public static void createFile(File f) {
        try {
            createParent(f);
            f.createNewFile();
        } catch (IOException e) {

        }
    }

    public static String strictRead(BufferedReader reader) {
        String cache;
        StringBuilder builder = new StringBuilder();
        try {
            while ((cache = reader.readLine()) != null)
                builder.append(cache).append("\n");
            reader.close();
        } catch (Exception e) {
            return "";
        }
        return builder.delete(builder.length() - 1, builder.length()).toString();
    }

    public static String readZip(String zip, String inZipFile) throws RuntimeException {
        try {
            FastBufferedInputStream is = new FastBufferedInputStream(new ZipFile(zip).getInputStream(new ZipEntry(inZipFile)));
            StringBuilder builder = new StringBuilder();
            int length;
            byte[] buf = new byte[1024];
            while ((length = is.read(buf)) != - 1) {
                String s = new String(buf, 0, length, StandardCharsets.UTF_8);
                builder.append(s);
            }
            is.close();
            return builder.toString();
        } catch (Exception e) {

        }
        return "";
    }

    public static String read(BufferedReader reader) {
        String cache;
        StringBuilder builder = new StringBuilder();
        try {
            while ((cache = reader.readLine()) != null)
                builder.append(cache).append("\n");
            reader.close();
        } catch (Exception e) {
            return "";
        }
        return builder.toString();
    }

    public static StringBuilder readAsStringBuilder(BufferedReader reader) {
        String cache;
        StringBuilder builder = new StringBuilder();
        try {
            while ((cache = reader.readLine()) != null)
                builder.append(cache).append("\n");
            reader.close();
        } catch (Exception e) {

        }
        return builder;
    }

    public static void write(File file, StringBuilder information) {
        write(file, information.toString());
    }

    public static void write(File file, String information) {
        try {
            createParent(file);
            FastBufferedOutputStream writer = new FastBufferedOutputStream(new FileOutputStream(file));
            writer.write(information.getBytes());
            writer.close();
        } catch (Exception e) {

        }
    }

    public static void write(File file, Collection<?> information) {
        try {
            createParent(file);
            FastBufferedOutputStream writer = new FastBufferedOutputStream(new FileOutputStream(file, true));

            for (Object o : information) {
                writer.write((o.toString() + "\n").getBytes());
            }

            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openInBrowse(String url) throws IOException {
        URI uri = URI.create(url);
        Desktop dp = Desktop.getDesktop();
        if (dp.isSupported(Desktop.Action.BROWSE)) {
            dp.browse(uri);
        }
    }

    public static void openInExplorer(String s) throws IOException {
        Runtime.getRuntime().exec(new String[]{"explorer.exe", "\"" + s.replace("/", "\\") + "\""});
    }

    public static void openInNautilus(String s) throws IOException {
        Runtime.getRuntime().exec(new String[]{"nautilus", s});
    }

    public static String absPath(String path) {
        return new File(path).getAbsolutePath();
    }

    public static void clone(String from, String to) {
        for (File f : Objects.requireNonNull(new File(from).listFiles())) {
            try {
                if (f.isDirectory()) {
                    if (Objects.requireNonNull(f.listFiles()).length > 0) {
                        clone(from + "/" + f.getName(), to + "/" + f.getName());
                    }
                } else if (f.isFile()) {
                    File toFile = new File(to + "/" + f.getName());
                    copy(f, toFile);
                }
            } catch (Exception e) {

            }
        }
    }

    public static void copy(String from, String to) throws Exception {
        copy(new File(from), new File(to));
    }

    public static void copy(File from, File to) throws Exception {
        if (!from.exists()) {
            return;
        }
        deleteFile(to);
        createFile(to);
        FastBufferedInputStream input = new FastBufferedInputStream(new FileInputStream(from));
        FileOutputStream outputStream = new FileOutputStream(to);
        FileChannel channel = outputStream.getChannel();
        FastBufferedOutputStream output = new FastBufferedOutputStream(outputStream);
        byte[] buff = new byte[8192];
        int length;
        while ((length = input.read(buff, 0, buff.length)) != - 1) {
            output.write(buff, 0, length);
        }
        input.close();
        output.close();
    }
}
