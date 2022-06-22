package com.github.cao.awa.modmdo.mixins.entity.list;

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
public abstract class EntityListMixin {
//    private final ReentrantLock lock = new ReentrantLock();
//    private final Object2ObjectOpenHashMap<Boolean, Entity> willProcess = new Object2ObjectOpenHashMap<>();
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
//        willProcess.forEach((add, entity) -> {
//            if (add) {
//                add(entity);
//            } else {
//                remove(entity);
//            }
//        });
//        willProcess.clear();
//
//        lock.lock();

        if (this.iterating != null) {
            throw new UnsupportedOperationException("Only one concurrent iteration supported");
        } else {
            this.iterating = this.entities;

            try {
                for (Entity entity : this.entities.values()) {
                    if (entity == null) {
                        continue;
                    }
                    if (SharedVariables.isActive()) {
                        if (cancelEntitiesTick) {
                            Identifier id = EntityType.getId(entity.getType());
                            if (id != null && id.toString().equals("minecraft:player")) {
                                action.accept(entity);
                                return;
                            }
                            continue;
                        }
                    }
                    action.accept(entity);
                }
            } finally {
                this.iterating = null;
//                lock.unlock();
            }
        }
    }

//    @Shadow
//    public abstract void add(Entity entity);
//
//    @Shadow
//    public abstract void remove(Entity entity);
//
//    @Inject(method = "add", at = @At("HEAD"), cancellable = true)
//    public void addStart(Entity entity, CallbackInfo ci) {
//        if (lock.isLocked()) {
//            willProcess.put(true, entity);
//            ci.cancel();
//        }
//    }
//
//    @Inject(method = "remove", at = @At("HEAD"), cancellable = true)
//    public void removeStart(Entity entity, CallbackInfo ci) {
//        if (lock.isLocked()) {
//            willProcess.put(false, entity);
//            ci.cancel();
//        }
//    }
}
