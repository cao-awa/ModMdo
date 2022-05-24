package com.github.zhuaidadaya.modmdo.event.trigger;

import com.github.zhuaidadaya.modmdo.event.entity.*;
import com.github.zhuaidadaya.modmdo.event.trigger.message.*;
import com.github.zhuaidadaya.modmdo.event.variable.*;
import com.github.zhuaidadaya.modmdo.simple.vec.*;
import com.github.zhuaidadaya.modmdo.utils.dimension.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.receptacle.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.entity.*;

import java.util.*;
import java.util.function.*;

import static com.github.zhuaidadaya.modmdo.storage.SharedVariables.variables;

public abstract class TargetedTrigger<T extends EntityTargetedEvent<?>> extends ModMdoEventTrigger<T> {
    public static final Object2ObjectArrayMap<String, BiConsumer<TargetedTrigger<?>, Receptacle<String>>> formatter = EntrustParser.operation(new Object2ObjectArrayMap<>(), map -> {
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
        map.put("^{variable}", (trigger, str) -> {
            str.set(variables.get(str.getSub()).get().toString());
        });
    });

    private ObjectArrayList<? extends Entity> target;

    public ObjectArrayList<? extends Entity> getTarget() {
        return target;
    }

    public void setTarget(ObjectArrayList<? extends Entity> target) {
        this.target = target;
    }
}
