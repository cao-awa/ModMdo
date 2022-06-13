package com.github.cao.awa.modmdo.event.trigger.summon;

import com.github.cao.awa.modmdo.annotations.*;
import com.github.cao.awa.modmdo.event.*;
import com.github.cao.awa.modmdo.event.trigger.*;
import com.github.cao.awa.modmdo.event.trigger.trace.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.operational.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.*;
import net.minecraft.world.*;
import org.json.*;

@Auto
public class SummonTrigger<T extends ModMdoEvent<?>> extends ModMdoEventTrigger<T> {
    private String id;
    private BlockPos pos;
    private int count;

    @Override
    public ModMdoEventTrigger<T> build(T event, JSONObject metadata, TriggerTrace trace) {
        setMeta(metadata);
        setTrace(trace);
        setServer(event.getServer());
        this.id = metadata.getString("identifier");
        JSONObject position = metadata.getJSONObject("pos");
        this.pos = new BlockPos(position.getInt("x"), position.getInt("y"), position.getInt("z"));
        this.count = EntrustParser.tryCreate(() -> metadata.getInt("count"), 1);
        return this;
    }

    @Override
    public void action() {
        EntrustExecution.tryTemporary(() -> {
            OperationalInteger integer = new OperationalInteger(count);
            while (integer.reduce() > -1) {
                Registry.ENTITY_TYPE.get(new Identifier(id)).spawn(getServer().getWorld(World.OVERWORLD), null, null, null, pos, SpawnReason.EVENT, false, false);
            }
        });
    }
}
