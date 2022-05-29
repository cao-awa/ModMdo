package com.github.cao.awa.modmdo.event.variable.integer;

import com.github.cao.awa.modmdo.annotations.*;
import com.github.cao.awa.modmdo.event.variable.integer.operation.*;
import com.github.cao.awa.modmdo.utils.times.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.operational.*;
import net.minecraft.util.*;
import org.json.*;

import java.io.*;

@Auto
public class PersistentAutoLong extends PersistentInteger<Long> {
    private OperationalLong value;
    private Pair<Long, Long> shouldChange;
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

    public Long get() {
        solve();
        return value.get();
    }

    public PersistentAutoLong clone() {
        solve();
        return new PersistentAutoLong().build(null, toJSONObject());
    }

    public PersistentAutoLong build(File file, JSONObject json) {
        setFile(file);
        setMeta(json);
        this.value = new OperationalLong(json.getInt("value"));
        this.interval = json.getLong("interval");
        this.shouldChange = new Pair<>(json.getLong("should-change-time"), json.getLong("amplifier"));
        this.name = json.getString("name");
        return this;
    }

    private void solve() {
        long change = (TimeUtil.processMillion(shouldChange.getLeft())) / Math.max(1, interval);
        value.add(change, shouldChange.getRight());
        shouldChange.setLeft(TimeUtil.millions());
        save();
    }

    public void handle(JSONObject json) {
        solve();
        IntegerOperation operation = IntegerOperation.of(json.getString("operation"));
        Long integer = json.getLong("value");
        switch (operation) {
            case ADD -> value.add(integer);
            case REDUCE -> value.reduce(integer);
            case MULTIPLY -> value.multiply(integer);
            case DIVIDE -> value.divide(integer);
            case SET -> value.set(integer);
        }
        save();
    }
}
