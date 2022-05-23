package com.github.zhuaidadaya.modmdo.mixins;

import it.unimi.dsi.fastutil.ints.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;
import org.spongepowered.asm.mixin.*;

import java.util.function.*;

import static com.github.zhuaidadaya.modmdo.storage.SharedVariables.*;

@Mixin(EntityList.class)
public class EntityListMixin {
    @Shadow
    private Int2ObjectMap<Entity> entities;
    @Shadow
    private @Nullable Int2ObjectMap<Entity> iterating;

    @Shadow
    private Int2ObjectMap<Entity> temp;

    /**
     * @author 草awa
     * @reason
     */
    @Overwrite
    public void forEach(Consumer<Entity> action) {
        if (extras != null && extras.isActive(EXTRA_ID)) {
            if (this.iterating != null) {
                throw new UnsupportedOperationException("Only one concurrent iteration supported");
            } else {
                this.iterating = this.entities;

                try {
                    for (Entity entity : this.entities.values()) {
                        if (entity == null) {
                            continue;
                        }
                        if (cancelEntitiesTick) {
                            Identifier id = EntityType.getId(entity.getType());
                            if (id != null && id.toString().equals("minecraft:player")) {
                                action.accept(entity);
                                return;
                            }
                            continue;
                        }
                        action.accept(entity);
                    }
                } finally {
                    this.iterating = null;
                }
            }
        }
    }

    /**
     * @author 草awa
     * @reason
     */
    @Overwrite
    private void ensureSafe() {
        if (this.iterating == this.entities) {
            try {
                this.temp.clear();

                for (Int2ObjectMap.Entry<Entity> entityEntry : Int2ObjectMaps.fastIterable(this.entities)) {
                    this.temp.put(entityEntry.getIntKey(), entityEntry.getValue());
                }

                Int2ObjectMap<Entity> int2ObjectMap = this.entities;
                this.entities = this.temp;
                this.temp = int2ObjectMap;
            } catch (Exception e) {

            }
        }
    }
}
