package com.github.cao.awa.modmdo.event.trigger.summon;

import com.github.cao.awa.modmdo.annotations.*;
import com.github.cao.awa.modmdo.event.*;
import com.github.cao.awa.modmdo.event.trigger.*;
import com.github.cao.awa.modmdo.event.trigger.trace.*;
import com.github.cao.awa.modmdo.utils.text.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.operational.*;
import net.minecraft.entity.*;
import net.minecraft.registry.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import org.json.*;

@Auto
public class SummonTrigger<T extends ModMdoEvent<?>> extends ModMdoEventTrigger<T> {
    private String id;
    // TODO: 2022/6/14
    //private String nbt;
    private String name;
    private String dimension;
    private boolean alignPosition;
    private boolean invertY;
    private BlockPos pos;
    private int count;

    @Override
    public ModMdoEventTrigger<T> prepare(T event, JSONObject metadata, TriggerTrace trace) {
        setServer(event.getServer());
        this.id = metadata.getString("identifier");
        JSONObject position = metadata.getJSONObject("pos");
        this.pos = new BlockPos(position.getInt("x"), position.getInt("y"), position.getInt("z"));
        this.count = EntrustParser.tryCreate(() -> metadata.getInt("count"), 1);
        // TODO: 2022/6/14
        //this.nbt = EntrustParser.trying(() -> metadata.getString("nbt"));
        this.dimension = metadata.getString("world");
        this.name = EntrustParser.trying(() -> metadata.getString("name"));
        this.alignPosition = EntrustParser.trying(() -> metadata.getBoolean("alignPosition"), ex -> false);
        this.invertY = EntrustParser.trying(() -> metadata.getBoolean("invertY"), ex -> false);
        return this;
    }

    @Override
    public void action() {
        EntrustExecution.tryTemporary(() -> {
            OperationalInteger integer = new OperationalInteger(count);
            while (integer.reduce() > - 1) {
                RegistryKey<World> world;
                if (dimension.equals(World.OVERWORLD.getValue().toString())) {
                    world = World.OVERWORLD;
                } else if (dimension.equals(World.NETHER.getValue().toString())) {
                    world = World.NETHER;
                } else {
                    world = World.END;
                }
                Registries.ENTITY_TYPE.get(new Identifier(id)).spawn(getServer().getWorld(world), null, entity -> entity.setCustomName(name == null ? null : TextUtil.literal(name).text()), pos, SpawnReason.EVENT, alignPosition, invertY);
            }
        });
    }
}
