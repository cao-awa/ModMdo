package com.github.cao.awa.modmdo.event.trigger.selector;

import com.github.cao.awa.modmdo.annotations.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.selector.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.selector.algorithm.*;
import org.json.*;

import java.util.function.*;

@Auto
public class RandomSelector extends TriggerSelector {
    private ObjectSelector<String, JSONObject> selector;

    @Override
    public void select(JSONObject json, BiConsumer<String, JSONObject> operation) {
        EntrustEnvironment.tryFor(json.keySet(), name -> getTargets().put(name, json.getJSONObject(name)));
        selector.select(getTargets());
        accept(operation);
    }

    @Override
    public void build(JSONObject json) {
        String algorithm = json.getString("algorithm");
        switch (algorithm) {
            case "exclude-select" -> selector = new ExcludeSelector<>(json.getInt("exclude"));
            case "free-select" -> selector = new FreeSelector<>();
            default -> throw new IllegalArgumentException("Algorithm \"" + algorithm + "\" not found");
        }
    }
}
