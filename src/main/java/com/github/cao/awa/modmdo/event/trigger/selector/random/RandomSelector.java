package com.github.cao.awa.modmdo.event.trigger.selector.random;

import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import org.json.*;

import java.util.*;
import java.util.function.*;

public class RandomSelector extends TriggerSelector {
    private int exclude = 0;
    private String algorithm = "exclude-select";

    @Override
    public void select(JSONObject json, BiConsumer<String, JSONObject> operation) {
        for (String name : json.keySet()) {
            targets.put(name, json.getJSONObject(name));
        }
        switch (algorithm) {
            case "exclude-select" -> excludeSelect(exclude);
            case "free-select" -> freeSelect();
        }
        select(operation);
    }

    public void select(BiConsumer<String, JSONObject> operation) {
        for (String name : targets.keySet()) {
            operation.accept(name, targets.get(name));
        }
    }

    @Override
    public void build(JSONObject json) {
        algorithm = json.getString("algorithm");
        switch (algorithm) {
            case "exclude-select" -> exclude = json.getInt("exclude");
            case "free-select" -> {

            }
            default -> throw new IllegalArgumentException("Algorithm \"" + algorithm + "\" not found");
        }
    }

    public void excludeSelect(int exclude) {
        List<String> keys = new ArrayList<>(targets.keySet());
        Random r = new Random();
        for (; exclude > 0 && keys.size() > 0; exclude--) {
            targets.remove(EntrustParser.desert(keys, r));
        }
    }

    public void freeSelect() {
        Random random = new Random();
        String name = EntrustParser.select(targets.keySet().stream().toList(), random);
        JSONObject json = targets.get(name);
        targets.clear();
        targets.put(name, json);
    }
}
