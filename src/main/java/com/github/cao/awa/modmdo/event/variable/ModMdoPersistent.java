package com.github.cao.awa.modmdo.event.variable;

import com.github.cao.awa.modmdo.utils.file.*;
import org.json.*;

import java.io.*;

public abstract class ModMdoPersistent<T> {
    public File file;
    public String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void save() {
        JSONObject json = new JSONObject();
        json.put("variable", toJSONObject());
        FileUtil.write(file, json.toString());
    }

    public abstract JSONObject toJSONObject();

    public abstract T get();

    public abstract void build(File file, JSONObject json);

    public abstract void handle(JSONObject json);
}
