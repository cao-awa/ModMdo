package com.github.zhuaidadaya.modMdo.bak;

import com.github.zhuaidadaya.MCH.times.TimeType;
import com.github.zhuaidadaya.MCH.times.Times;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.*;
import java.util.UUID;

public class Backup {
    private final Logger logger = LogManager.getLogger("ModMdo Backup");
    private final String name;
    private final String sourcePath;
    private final String id;
    private String path;
    private String size;
    private String createTime;
    private long totalSize = 0;
    private boolean synchronizing = false;
    private boolean stopping = false;
    private boolean stopped = false;

    public Backup(JSONObject json) {
        name = json.get("name").toString();
        path = json.get("path").toString();
        sourcePath = json.get("sourcePath").toString();
        size = json.get("size").toString();
        createTime = json.get("createTime").toString();
        id = json.get("id").toString();
    }

    public Backup(String name, String toPath, String sourcePath) {
        this.id = UUID.randomUUID().toString();
        if(name == null)
            this.name = this.id;
        else
            this.name = name;
        this.path = toPath;
        this.sourcePath = sourcePath;
        this.createTime = Times.getTime(TimeType.AS_SECOND);
        this.size = "0MB";
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getSize() throws SyncFailedException {
        if(synchronizing)
            throw new SyncFailedException("cannot invoke setPath() because synchronizing");
        return size;
    }

    public Backup setPath(String path) throws SyncFailedException {
        if(synchronizing)
            throw new SyncFailedException("cannot invoke setPath() because synchronizing");
        synchronizing = true;
        this.path = path;
        synchronizing = false;
        return this;
    }

    public Backup setSourcePath(String sourcePath) throws SyncFailedException {
        if(synchronizing)
            throw new SyncFailedException("cannot invoke setSourcePath() because synchronizing");
        synchronizing = true;
        this.path = sourcePath;
        synchronizing = false;
        return this;
    }

    public void stop() {
        stopping = true;

        while(! stopped) {
            try {
                Thread.sleep(25);
            } catch (InterruptedException e) {

            }
        }

        synchronizing = false;

        stopped = false;
    }

    public boolean isSynchronizing() {
        return synchronizing;
    }

    public long createBackup() throws SyncFailedException {
        if(synchronizing)
            throw new SyncFailedException("cannot invoke createBackup() because synchronizing");

        synchronizing = true;

        long startTime = System.currentTimeMillis();

        createTime = Times.getTime(TimeType.AS_SECOND);

        writeFiles(new File(sourcePath), path + "/");

        if(stopping) {
            deleteFiles(new File(path), path + "/");

            new File(path).delete();

            stopped = true;

            synchronizing = false;

            stopping = false;

            return - 1;
        } else {
            size = totalSize / 1024 / 1024 + "MB";

            synchronizing = false;

            return System.currentTimeMillis() - startTime;
        }
    }

    private void deleteFiles(File file, String base) {
        if(file.isDirectory()) {
            for(File f : file.listFiles()) {
                if(f.isFile()) {
                    f.delete();
                } else if(f.isDirectory()) {
                    deleteFiles(f, base + f.getName() + "/");
                    f.delete();
                }
            }
        } else {
            file.delete();
        }
    }

    private void writeFiles(File file, String base) {
        if(file.isDirectory()) {
            for(File f : file.listFiles()) {
                if(stopping)
                    break;

                if(f.isFile()) {
                    try {
                        File parent = new File(base);
                        if(! parent.isDirectory())
                            parent.mkdirs();

                        if(f.getAbsolutePath().contains("$"))
                            continue;

                        writeSingleFile(new File(base + f.getName()), f);
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error("fail to backup file: " + f.getAbsolutePath());
                    }
                } else if(f.isDirectory()) {
                    writeFiles(f, base + f.getName() + "/");
                }
            }
        } else {
            try {
                writeSingleFile(new File(base + file.getName()), file);
            } catch (Exception e) {
                logger.error("fail to backup file: " + file.getAbsolutePath());
            }
        }
    }

    private void writeSingleFile(File toFile, File fromFile) throws Exception {
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(toFile));
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(fromFile));

        byte[] buffer = new byte[8192];
        int bytes;

        try {
            while((bytes = in.read(buffer)) != - 1) {
                if(stopping)
                    break;
                out.write(buffer, 0, bytes);
                totalSize += bytes;
            }
        } catch (Exception e) {

        }

        out.close();
        in.close();
    }

    public JSONObject toJSONObject() throws SyncFailedException {
        if(synchronizing)
            throw new SyncFailedException("cannot invoke toJSONObject() because synchronizing");
        synchronizing = true;
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("id", id);
        json.put("size", size);
        json.put("totalSize", totalSize);
        json.put("path", path);
        json.put("sourcePath", sourcePath);
        json.put("createTime", createTime);
        synchronizing = false;
        return json;
    }
}
