package com.github.zhuaidadaya.modmdo.mixins;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.Entity;
import net.minecraft.world.EntityList;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Consumer;

import static com.github.zhuaidadaya.modmdo.storage.Variables.enabledCancelEntitiesTIck;

@Mixin(EntityList.class)
public class EntityListMixin {

    @Shadow private Int2ObjectMap<Entity> entities;

    @Shadow private @Nullable Int2ObjectMap<Entity> iterating;

    @Shadow private Int2ObjectMap<Entity> temp;

    /**
     * @author 草awa
     *
     * @reason
     */
    @Overwrite
    public void forEach(Consumer<Entity> action) {
        if(enabledCancelEntitiesTIck) {
            return;
        }

        if (this.iterating != null) {
            throw new UnsupportedOperationException("Only one concurrent iteration supported");
        } else {
            this.iterating = this.entities;

            try {
                for(Entity entity : this.entities.values()) {
                    action.accept(entity);
                }
            } finally {
                this.iterating = null;
            }
        }
    }
}
