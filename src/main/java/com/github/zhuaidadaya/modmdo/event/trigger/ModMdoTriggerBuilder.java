package com.github.zhuaidadaya.modmdo.event.trigger;

import com.github.zhuaidadaya.modmdo.event.*;
import com.github.zhuaidadaya.modmdo.event.entity.*;
import com.github.zhuaidadaya.modmdo.event.entity.death.*;
import com.github.zhuaidadaya.modmdo.event.entity.player.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import org.json.*;

import java.util.function.*;

public class ModMdoTriggerBuilder {
    public void register(JSONObject json) {
        JSONObject ev = json.getJSONObject("event");
        switch (ev.getString("name")) {
            case "com.github.zhuaidadaya.modmdo.event.entity.death.EntityDeathEvent" -> {
                ModMdoEventCenter.registerEntityDeath(buildEntityDeath(ev));
            }
            case "com.github.zhuaidadaya.modmdo.event.entity.player.JoinServerEvent" -> {
                ModMdoEventCenter.registerJoinServer(buildJoinServer(ev));
            }
        }
    }

    public Consumer<EntityDeathEvent> buildEntityDeath(JSONObject event) {
        return e -> prepareTargeted(event, e);
    }

    public void prepareTargeted(JSONObject event, EntityTargetedEvent<?> targeted) {
        final boolean instance = EntrustParser.trying(() -> event.getString("instanceof").equals(targeted.getTargeted().getClass().getName()), () -> true);
        if (instance) {
            JSONObject triggers = event.getJSONObject("triggers");
            for (String o : triggers.keySet()) {
                JSONObject json = triggers.getJSONObject(o);
                EntrustExecution.notNull(EntrustParser.trying(() -> {
                    ModMdoEventTrigger<EntityTargetedEvent<?>> trigger = (ModMdoEventTrigger<EntityTargetedEvent<?>>) Class.forName(json.getString("name")).getDeclaredConstructor().newInstance();
                    trigger.build(targeted, json);
                    return trigger;
                }, () -> null), ModMdoEventTrigger::action);
            }
        }
    }

    public Consumer<JoinServerEvent> buildJoinServer(JSONObject event) {
        return e -> prepareTargeted(event, e);
    }
}
