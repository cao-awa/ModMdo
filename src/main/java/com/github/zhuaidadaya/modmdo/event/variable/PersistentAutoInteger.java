package com.github.zhuaidadaya.modmdo.event.variable;

import com.github.zhuaidadaya.modmdo.reads.*;
import com.github.zhuaidadaya.modmdo.utils.times.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.annotaions.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.operational.*;
import net.minecraft.util.*;
import org.json.*;

import java.io.*;

public class PersistentAutoInteger extends ModMdoPersistent<Integer> {
    private OperationalInteger value;
    private Pair<Long, Integer> shouldChange;
    private long interval;

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        json.put("value", value.get());
        json.put("interval", interval);
        json.put("should-change-time", shouldChange.getLeft());
        json.put("amplifier", shouldChange.getRight());
        json.put("instanceof", getClass().getName());
        json.put("name", name);
        return json;
    }

    public Integer get() {
        solve();
        return value.get();
    }

    public void build(File file, JSONObject json) {
        setFile(file);
        this.value = new OperationalInteger(json.getInt("value"));
        this.interval = json.getLong("interval");
        this.shouldChange = new Pair<>(json.getLong("should-change-time"), json.getInt("amplifier"));
        this.name = json.getString("name");
    }

    private void solve() {
        long change = (TimeUtil.processMillion(shouldChange.getLeft())) / Math.max(1, interval);
        value.add(change, shouldChange.getRight());
        shouldChange.setLeft(TimeUtil.millions() + interval);
        save();
    }
}
