package com.github.zhuaidadaya.modMdo.mixins;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.Entity;
import net.minecraft.world.EntityList;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.LinkedHashMap;
import java.util.function.Consumer;

import static com.github.zhuaidadaya.modMdo.storage.Variables.*;

@Mixin(EntityList.class)
public class EntityListMixin {
    @Shadow
    private Int2ObjectMap<Entity> entities;

    @Shadow
    private @Nullable Int2ObjectMap<Entity> iterating;

    /**
     * @author 草awa
     * @reason
     */
    @Overwrite
    public void forEach(Consumer<Entity> action) {
        if(enableTickAnalyzer) {
            try {
                tickMap.put("tick_world" + tickMap.get("ticking_world") + "_entities_start", System.currentTimeMillis());
                tickMap.put("world" + tickMap.get("ticking_world") + "_entities", (long) entities.size());
            } catch (Exception e) {

            }
        }

        if(this.iterating != null) {
            throw new UnsupportedOperationException("Only one concurrent iteration supported");
        } else {
            this.iterating = this.entities;
            LinkedHashMap<String, Integer> worldEntities = new LinkedHashMap<>();
            try {
                for(Entity entity : this.entities.values()) {
                    try {
                        String name = entity.getType().toString();
                        worldEntities.put(name, worldEntities.containsKey(name) ? worldEntities.get(name) + 1 : 1);
                    } catch (Exception e) {

                    }
                    action.accept(entity);
                }
            } finally {
                tickEntitiesMap.put("world" + tickMap.get("ticking_world") + "_entities", worldEntities);
                this.iterating = null;
            }
        }

        if(enableTickAnalyzer) {
            try {
                tickMap.put("tick_world" + tickMap.get("ticking_world") + "_entities_time", System.currentTimeMillis() - tickMap.get("tick_world" + tickMap.get("ticking_world") + "_entities_start"));
            } catch (Exception e) {

            }
        }
    }
}
