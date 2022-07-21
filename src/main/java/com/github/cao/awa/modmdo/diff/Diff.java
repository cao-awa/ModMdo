package com.github.cao.awa.modmdo.diff;

import com.github.cao.awa.modmdo.utils.file.*;
import com.github.zhuaidadaya.rikaishinikui.handler.conductor.string.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import it.unimi.dsi.fastutil.objects.*;

import java.io.*;
import java.util.*;

public class Diff {
    private final Object2ObjectOpenHashMap<String, String> diffs = new Object2ObjectOpenHashMap<>();
    private final String diffFile;

    public Diff(String diffFile) {
        this.diffFile = diffFile;
        load();
    }

    public void load() {
        if (diffFile != null && new File(diffFile).isFile()) {
            EntrustExecution.tryTemporary(() -> {
                String diff = FileUtil.strictRead(new BufferedReader(new FileReader(diffFile)));
                StringTokenizerConductor conductor = new StringTokenizerConductor(new StringTokenizer(diff, "\n"));
                for (String s : conductor) {
                    diffs.put(s.substring(0, s.indexOf(" ")), s.substring(s.indexOf(" ") + 1));
                }
            });
        }
    }

    public boolean hasDiffs() {
        return diffs.size() > 0;
    }

    private void load(String diffFile) {
        EntrustExecution.tryTemporary(() -> {
            String diff = FileUtil.read(new BufferedReader(new FileReader(diffFile)));
        });
    }

    public void save() {
        StringBuilder builder = new StringBuilder();

        for (String key : diffs.keySet()) {
            builder.append(key).append(" ").append(diffs.get(key)).append("\n");
        }

        FileUtil.write(new File(diffFile), builder);
    }

    public void updateDiff(String target, String value) {
        diffs.put(target, value);
    }

    public boolean conflict(String target, String  value) {
        if (value == null) {
            return true;
        }
        return !value.equals(diffs.get(target));
    }

    public String getDiffFile() {
        return diffFile;
    }
}
