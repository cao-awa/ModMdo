package com.github.cao.awa.modmdo.backup;

import com.github.cao.awa.modmdo.diff.*;
import com.github.cao.awa.modmdo.utils.file.*;
import com.github.cao.awa.modmdo.utils.times.*;
import org.json.*;

import java.io.*;
import java.util.*;
import java.util.function.*;

public class VersionControlBackup extends Backup {
    private long time;
    private String source;
    private int id;
    private Diff diff;
    private Diff centerDiff;

    public VersionControlBackup(JSONObject json) {
        revert(json);
    }

    public VersionControlBackup(String source, int id) {
        this.source = source;
        this.id = id;
        this.diff = new Diff("backups/modmdo/version_controller/or_" + id + "/backup.diff");
        buildCenter();
    }

    private void buildCenter() {
        this.centerDiff = new Diff("backups/modmdo/version_controller/center/backup.diff");
        backup(centerDiff, centerDiff);
    }

    public void backup() {
        backup(diff, centerDiff);
    }

    private void backup(Diff diff, Diff center) {
        foreach("", new File(source), (source, to) -> {
            String sha = FileUtil.fileSha(source, MessageDigger.Sha3.SHA_256);
            if (diff.conflict(source.getAbsolutePath(), sha)) {
                if (center.conflict(source.getAbsolutePath(), sha)) {
                    FileUtil.copy(source, new File("backups/modmdo/version_controller/or_" + id + "/backup/" + to.getPath()));
                    FileUtil.copy(source, new File("backups/modmdo/version_controller/center/backup/" + to.getPath()));
                    diff.updateDiff(source.getAbsolutePath(), sha);
                } else {
//                    System.out.println("oh");
                }
            }
        });

        time = TimeUtil.millions();

        diff.save();
    }

    private void foreach(String base, File source, BiConsumer<File, File> action) {
        for (File file : Objects.requireNonNull(source.listFiles())) {
            if (file.isFile()) {
                action.accept(file, new File(base + "/" + file.getName()));
            } else {
                foreach(base + "/" + file.getName(), file, action);
            }
        }
    }

    @Override
    public JSONObject toJSONObject() {
        return new JSONObject().put("diff", diff.getDiffFile()).put("source", source).put("id", id).put("time", time);
    }

    public void revert(JSONObject json) {
        this.source = json.getString("source");
        this.id = json.getInt("id");
        this.diff = new Diff(json.getString("diff"));
        this.time = json.getLong("time");
    }
}
