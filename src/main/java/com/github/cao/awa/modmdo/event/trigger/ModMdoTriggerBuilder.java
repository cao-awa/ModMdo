package com.github.cao.awa.modmdo.event.trigger;

import com.github.cao.awa.modmdo.event.*;
import com.github.cao.awa.modmdo.event.entity.*;
import com.github.cao.awa.modmdo.event.trigger.selector.*;
import com.github.cao.awa.modmdo.event.trigger.selector.random.*;
import com.github.cao.awa.modmdo.event.trigger.trace.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.operational.*;
import org.json.*;

import java.io.*;

public class ModMdoTriggerBuilder {
    public void register(JSONObject json, File trace) {
        JSONObject e = json.getJSONObject("event");
        String name = e.getString("instanceof");
        switch (name) {
            case "com.github.cao.awa.modmdo.event.entity.death.EntityDeathEvent" -> {
                ModMdoEventCenter.registerEntityDeath(event -> prepareTargeted(e, event, trace));
            }
            case "com.github.cao.awa.modmdo.event.entity.player.JoinServerEvent" -> {
                ModMdoEventCenter.registerJoinServer(event -> prepareTargeted(e, event, trace));
            }
            case "com.github.cao.awa.modmdo.event.server.tick.GameTickStartEvent" -> {
                ModMdoEventCenter.registerGameTickStart(event -> prepareTargeted(e, event, trace));
            }
            case "com.github.cao.awa.modmdo.event.server.ServerStartedEvent" -> {
                ModMdoEventCenter.registerServerStarted(event -> prepare(e, event, trace));
            }
            default -> {
                throw new IllegalArgumentException("Event \"" + name + "\" not found, may you got key it wrong? will be not register this event");
            }
        }
    }

    public void prepareTargeted(JSONObject event, EntityTargetedEvent<?> targeted, File trace) {
        String instance = EntrustParser.trying(() -> event.getString("target-instanceof"));
        if (targeted.getTargeted().size() > 1 || EntrustParser.trying(() -> instance.equals(targeted.getTargeted().get(0).getClass().getName()), () -> true)) {
            TriggerSelector random = event.has("controller") ? controller(event.getJSONObject("controller")) : new AllSelector();
            OperationalInteger i = new OperationalInteger();
            random.select(event.getJSONObject("triggers"), (name, json) -> {
                EntrustExecution.notNull(EntrustParser.trying(() -> {
                    TargetedTrigger<EntityTargetedEvent<?>> trigger = (TargetedTrigger<EntityTargetedEvent<?>>) Class.forName(json.getString("instanceof")).getDeclaredConstructor().newInstance();
                    return trigger.build(targeted, json, new TriggerTrace(trace, i.get(), name));
                }, ex -> null), ModMdoEventTrigger::action);
                i.add();
            });
        }
    }

    public TriggerSelector controller(JSONObject json) {
        return EntrustParser.trying(() -> {
            TriggerSelector selector = (TriggerSelector) Class.forName(json.getString("instanceof")).getDeclaredConstructor().newInstance();
            selector.build(json);
            return selector;
        }, AllSelector::new);
    }

    public void prepare(JSONObject event, ModMdoEvent<?> targeted, File trace) {
        TriggerSelector selector = event.has("controller") ? controller(event.getJSONObject("controller")) : new AllSelector();
        OperationalInteger i = new OperationalInteger();
        selector.select(event.getJSONObject("triggers"), (name, json) -> {
            EntrustExecution.notNull(EntrustParser.trying(() -> {
                ModMdoEventTrigger<ModMdoEvent<?>> trigger = (ModMdoEventTrigger<ModMdoEvent<?>>) Class.forName(json.getString("instanceof")).getDeclaredConstructor().newInstance();
                return trigger.build(targeted, json, new TriggerTrace(trace, i.get(), name));
            }, ex -> null), ModMdoEventTrigger::action);
            i.add();
        });
    }
}
