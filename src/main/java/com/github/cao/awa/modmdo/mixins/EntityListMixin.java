package com.github.cao.awa.modmdo.mixins;

import com.github.cao.awa.modmdo.storage.*;
import it.unimi.dsi.fastutil.ints.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;
import org.spongepowered.asm.mixin.*;

import java.util.function.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

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
        if (SharedVariables.isActive()) {
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
}
