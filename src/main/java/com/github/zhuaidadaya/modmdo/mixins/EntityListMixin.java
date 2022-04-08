package com.github.zhuaidadaya.modmdo.mixins;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.world.EntityList;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Consumer;

import static com.github.zhuaidadaya.modmdo.storage.Variables.cancelEntitiesTick;

@Mixin(EntityList.class)
public class EntityListMixin {
    @Shadow
    private Int2ObjectMap<Entity> entities;
    @Shadow
    private @Nullable Int2ObjectMap<Entity> iterating;

    @Shadow private Int2ObjectMap<Entity> temp;

    /**
     * @author 草awa
     * @reason
     */
    @Overwrite
    public void forEach(Consumer<Entity> action) {
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
