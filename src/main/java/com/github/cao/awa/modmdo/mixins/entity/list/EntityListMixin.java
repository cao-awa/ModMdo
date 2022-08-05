package com.github.cao.awa.modmdo.mixins.entity.list;

import it.unimi.dsi.fastutil.ints.*;
import net.minecraft.entity.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;
import org.spongepowered.asm.mixin.*;

import java.util.*;
import java.util.function.*;

@Mixin(EntityList.class)
public abstract class EntityListMixin {
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
        if (this.iterating != null) {
            throw new UnsupportedOperationException("Only one concurrent iteration supported");
        } else {
            this.iterating = this.entities;

            try {
                entities.values().stream().filter(Objects::nonNull).forEach(action);
            } finally {
                this.iterating = null;
            }
        }
    }
}
