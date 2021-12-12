package com.github.zhuaidadaya.modMdo.bak;

import java.util.UUID;

public class AutoBackup {
    private String sourcePath;
    private String backupToPath;
    private String entrust;
    private String id;

    public AutoBackup(String sourcePath,String backupToPath,String entrust) {
        this.sourcePath = sourcePath;
        this.backupToPath = backupToPath;
        this.entrust = entrust;
        createID();
    }

    public AutoBackup(String sourcePath,String backupToPath) {
        this.sourcePath = sourcePath;
        this.backupToPath = backupToPath;
        createID();
    }

    private void createID() {
        id = UUID.randomUUID().toString();
    }

    public AutoBackup setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
        return this;
    }

    public AutoBackup setBackupToPath(String backupToPath) {
        this.backupToPath = backupToPath;
        return this;
    }

    public void backup() {

    }
}
