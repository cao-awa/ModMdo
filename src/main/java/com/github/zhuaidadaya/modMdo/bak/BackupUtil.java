package com.github.zhuaidadaya.modMdo.bak;

import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.SyncFailedException;
import java.util.LinkedHashMap;

import static com.github.zhuaidadaya.modMdo.storage.Variables.updateBackups;

public class BackupUtil {
    private final LinkedHashMap<Object, Backup> backups = new LinkedHashMap<>();
    private final Logger logger = LogManager.getLogger("ModMdo Backup");
    private boolean synchronizing = false;
    private Backup backupSnap;

    public BackupUtil(Backup... backups) {
        for(Backup backup : backups) {
            this.backups.put(backup.getName(), backup);
        }
    }

    public BackupUtil(JSONObject backups) {
        for(Object o : backups.keySet()) {
            JSONObject json = backups.getJSONObject(o.toString());

            this.backups.put(json.get("name"), new Backup(json));
        }
    }

    public boolean isSynchronizing() {
        return synchronizing;
    }

    public TranslatableText createBackup(Backup backup) throws Exception{
        backups.put(backup.getId(), backup);
        updateBackups();
        try {
            if(synchronizing)
                throw new SyncFailedException("cannot invoke createBackup() because synchronizing");
            synchronizing = true;
            backupSnap = backup;
            long backupTime = backupSnap.createBackup();
            if(backupTime == -1) {
                backups.remove(backup.getId());
                throw new Exception();
            }
            String time = (backupTime) + "ms(" + (backupTime / 1000) + "s)";
            String size = backupSnap.getSize();
            logger.info("backup finished in " + time + "s, " + "size: " + size);
            synchronizing = false;
            return new TranslatableText("backup.success",  time, size);
        } catch (SyncFailedException e) {
            logger.error("fail to backup: " + backupSnap.getId(), e);
            return new TranslatableText("backup.failed");
        }
    }

    public TranslatableText stop() {
        if(synchronizing) {
            if(backupSnap.isSynchronizing()) {
                backupSnap.stop();
            }
            synchronizing = false;
            return new TranslatableText("backup.stopped");
        } else {
            return new TranslatableText("backup.no.task");
        }
    }

    public JSONObject toJSONObject() throws SyncFailedException {
        if(synchronizing)
            throw new SyncFailedException("cannot invoke toJSONObject() because synchronizing");
        JSONObject json = new JSONObject();
        for(Object o : backups.keySet()) {
            try {
                json.put(o.toString(), backups.get(o).toJSONObject());
            } catch (SyncFailedException e) {

            }
        }
        return json;
    }
}
