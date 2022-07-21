package com.github.cao.awa.modmdo.backup;

import com.github.cao.awa.modmdo.diff.*;
import com.github.cao.awa.modmdo.utils.file.*;
import org.json.*;

import java.io.*;
import java.util.*;
import java.util.function.*;

public class DiffBackup extends Backup {
    private final Diff diff = new Diff("backups/modmdo/diff/backup.diff");
    private final String source;

    public DiffBackup(String source) {
        this.source = source;
    }

    @Override
    public JSONObject toJSONObject() {
        return new JSONObject();
    }

    public void backup() {
        foreach("", new File(source), (source, to) -> {
            String sha = FileUtil.fileSha(source, MessageDigger.Sha3.SHA_256);
            if (diff.conflict(source.getAbsolutePath(), sha)) {
                FileUtil.copy(source, to);
                diff.updateDiff(source.getAbsolutePath(), sha);
            }
        });

        diff.save();
    }

    private void foreach(String base, File source, BiConsumer<File, File> action) {
        for (File file : Objects.requireNonNull(source.listFiles())) {
            if (file.isFile()) {
                action.accept(file, new File("backups/modmdo/diff/backup/" + base + "/" + file.getName()));
            } else {
                foreach(base + "/" + file.getName(),file, action);
            }
        }
    }
}
