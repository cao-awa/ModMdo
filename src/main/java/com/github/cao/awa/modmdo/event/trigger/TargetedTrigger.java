package com.github.cao.awa.modmdo.event.trigger;

import com.github.cao.awa.modmdo.annotations.*;
import com.github.cao.awa.modmdo.event.entity.*;
import com.github.cao.awa.modmdo.simple.vec.*;
import com.github.cao.awa.modmdo.utils.dimension.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.collection.list.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.receptacle.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.entity.*;

import java.util.function.*;

@Auto
public abstract class TargetedTrigger<T extends EntityTargetedEvent<?>> extends ModMdoEventTrigger<T> {
    public static final Object2ObjectArrayMap<String, BiConsumer<TargetedTrigger<?>, Receptacle<String>>> TARGETED_FORMATTER = EntrustParser.operation(new Object2ObjectArrayMap<>(), map -> {
        map.put("%{dim_name}", (trigger, str) -> {
            str.set(DimensionUtil.getDimension(trigger.target.get(0).getEntityWorld()));
        });
        map.put("%{dim_color}", (trigger, str) -> {
            str.set(DimensionUtil.getDimensionColor(DimensionUtil.getDimension(trigger.target.get(0).getEntityWorld())));
        });
        map.put("%{pos}", (trigger, str) -> {
            str.set(new XYZ(trigger.target.get(0).getPos()).toString(2));
        });
        map.put("%{target_name}", (trigger, str) -> {
            String name = trigger.target.get(0).getName().asString();
            if (name.equals("")) {
                str.set(trigger.target.toString());
            } else {
                str.set(name);
            }
        });
    });

    private ObjectArrayList<? extends Entity> target;

    public ObjectArrayList<? extends Entity> getTarget() {
        return target;
    }

    public void setTarget(ObjectArrayList<? extends Entity> target) {
        this.target = target;
    }

    public abstract UnmodifiableListReceptacle<String> supported();
}
