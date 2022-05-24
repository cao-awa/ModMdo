package com.github.zhuaidadaya.modmdo.event.variable.integer;

import com.github.zhuaidadaya.modmdo.event.variable.*;
import com.github.zhuaidadaya.modmdo.event.variable.integer.operation.*;
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
        this.value = new OperationalInteger(Integer.parseInt(json.get("value").toString()));
        this.interval = Long.parseLong(json.get("interval").toString());
        this.shouldChange = new Pair<>(Long.parseLong(json.get("should-change-time").toString()), Integer.parseInt(json.get("amplifier").toString()));
        this.name = json.getString("name");
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
        Integer integer = json.getInt("value");
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
