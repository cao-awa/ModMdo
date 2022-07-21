package com.github.cao.awa.modmdo.backup;

import com.github.cao.awa.modmdo.backup.share.*;
import it.unimi.dsi.fastutil.objects.*;
import org.json.*;

public class ShareBackup extends Sharing {
    private final String source;
    private final int slot;

    public ShareBackup(String source, int slot) {
        this.source = source;
        this.slot = slot;
    }

    @Override
    public Object2ObjectOpenHashMap<String, String> detect() {
        return null;
    }

    @Override
    public void affect(Object2ObjectOpenHashMap<String, String> diffs) {

    }

    @Override
    public void action() {

    }

    @Override
    public void offline() {

    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        json.put("source", source);
        json.put("slot", slot);
        return json;
    }
}
