package com.github.cao.awa.modmdo.event.trigger.selector.entity;

import com.github.cao.awa.modmdo.event.trigger.*;
import com.github.cao.awa.modmdo.utils.entity.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.entity.*;
import com.alibaba.fastjson2.*;

import java.util.*;
import java.util.function.*;

public class EntitySelector {
    private final Object2ObjectArrayMap<EntitySelectorType, Consumer<String>> actions = new Object2ObjectArrayMap<>();
    private EntitySelectorType type;
    private String target;
    private final Collection<String> excepts = new ObjectArrayList<>();

    public EntitySelector(JSONObject json, TargetedTrigger<?> trigger) {
        EntitySelectorType type = EntitySelectorType.of(json.getString("type"));
        if (json.containsKey("target")) {
            String target = json.getString("target");
            if (type == EntitySelectorType.APPOINT) {
                build(type, target);
            } else if (trigger.supported(target)) {
                build(type, ModMdoTriggerBuilder.classMap.get(target));
            } else {
                throw new IllegalArgumentException("The \"" + target + "\" is not supported in trigger \"" + trigger.getClass().getName() + "\"");
            }
        } else {
            build(type, null);
        }
        if (json.containsKey("excepts")) {
            for (Object o : json.getJSONArray("excepts")) {
                if (ModMdoTriggerBuilder.classMap.containsKey(o.toString())) {
                    excepts.add(ModMdoTriggerBuilder.classMap.get(o.toString()));
                } else {
                    excepts.add(o.toString());
                }
            }
        }
    }

    public void build(EntitySelectorType type, String target) {
        this.type = type;
        this.target = target;
    }

    public void prepare(EntitySelectorType type, Consumer<String> action) {
        actions.put(type, action);
    }

    public void action() {
        actions.get(type).accept(target);
    }

    public void filter(Collection<? extends Entity> entities, Consumer<Entity> action) {
        for (Entity entity : entities) {
            filter(entity, action);
        }
    }

    public void filter(Entity entity, Consumer<Entity> action) {
        if (target == null) {
            accept(entity, action);
            return;
        }
        if (entity.getClass().getName().equals(target)) {
            accept(entity, action);
        }
    }

    private void accept(Entity entity, Consumer<Entity> action) {
        if (!excepts.contains(entity.getClass().getName())) {
            if (!excepts.contains(EntityUtil.getName(entity)) && !excepts.contains(EntityUtil.getUUID(entity).toString())) {
                action.accept(entity);
            }
        }
    }
}
