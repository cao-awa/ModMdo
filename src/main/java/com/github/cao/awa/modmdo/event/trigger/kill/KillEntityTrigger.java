package com.github.cao.awa.modmdo.event.trigger.kill;

import com.github.cao.awa.modmdo.annotations.*;
import com.github.cao.awa.modmdo.event.entity.*;
import com.github.cao.awa.modmdo.event.trigger.*;
import com.github.cao.awa.modmdo.event.trigger.selector.entity.*;
import com.github.cao.awa.modmdo.event.trigger.trace.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import net.minecraft.entity.*;
import org.json.*;

import static com.github.cao.awa.modmdo.event.trigger.selector.entity.EntitySelectorType.*;

@Auto
public class KillEntityTrigger<T extends EntityTargetedEvent<?>> extends TargetedTrigger<T> {
    private EntitySelector selector;

    @Override
    public ModMdoEventTrigger<T> prepare(T event, JSONObject metadata, TriggerTrace trace) {
        setTarget(event.getTargeted());
        setServer(event.getServer());
        selector = new EntitySelector(metadata.getJSONObject("selector"), this);
        return this;
    }

    @Override
    public void action() {
        EntrustEnvironment.trys(() -> {
            selector.prepare(SELF, target -> {
                if (getTarget().size() > 1) {
                    err("Cannot use \"SELF\" selector in targeted more than one", new IllegalArgumentException("Unable to process \"SELF\" selector for entities, it need appoint an entity"));
                    return;
                }
                getTarget().get(0).kill();
            });
            selector.prepare(WORLD, target -> {
                EntrustEnvironment.notNull(getServer().getWorld(getTarget().get(0).world.getRegistryKey()), world -> world.iterateEntities().forEach(entity -> {
                    selector.filter(entity, Entity::kill);
                }));
            });
            selector.prepare(ALL, target -> {
                getServer().getWorlds().forEach(world -> world.iterateEntities().forEach(entity -> selector.filter(entity, Entity::kill)));
            });
            selector.prepare(APPOINT, target -> {
                EntrustEnvironment.notNull(getServer().getPlayerManager().getPlayer(target), LivingEntity::kill);
            });
            selector.action();
        });
    }

    @Override
    public boolean supported(String targeted) {
        return ModMdoTriggerBuilder.classMap.containsKey(targeted);
    }
}
