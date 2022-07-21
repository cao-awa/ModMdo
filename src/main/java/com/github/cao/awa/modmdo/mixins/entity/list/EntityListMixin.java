package com.github.cao.awa.modmdo.mixins.entity.list;

import it.unimi.dsi.fastutil.ints.*;
import net.minecraft.entity.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;
import org.spongepowered.asm.mixin.*;

import java.util.function.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Mixin(EntityList.class)
public abstract class EntityListMixin {
    @Shadow
    private Int2ObjectMap<Entity> entities;
    @Shadow
    private @Nullable Int2ObjectMap<Entity> iterating;
    @Shadow
    private Int2ObjectMap<Entity> temp;

    @Shadow public abstract void remove(Entity entity);

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
                if (testingParallel) {
                    this.iterating.values().parallelStream().forEach(action);
                } else {
                    this.iterating.values().forEach(action);
                }
            } finally {
                this.iterating = null;
            }
        }
    }
}
