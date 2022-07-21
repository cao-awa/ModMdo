package com.github.cao.awa.modmdo.mixins.entity.selection;

import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.util.*;
import net.minecraft.util.collection.*;
import net.minecraft.world.entity.*;
import org.spongepowered.asm.mixin.*;

import java.util.*;
import java.util.function.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Mixin(EntityTrackingSection.class)
public class EntityTrackingSectionMixin<T> {
    private final Collection<T> waitingAdd = Collections.synchronizedCollection(new ObjectArrayList<>());
    private final Collection<T> waitingRemove = Collections.synchronizedCollection(new ObjectArrayList<>());
    @Shadow
    @Final
    private TypeFilterableList<T> collection;
    @Shadow
    private EntityTrackingStatus status;

    public void add(T obj) {
//        if (iterating) {
            waitingAdd.add(obj);
            informTask.submit("EntityTrackingSection: solve_waiting_add", () -> {
                this.waitingAdd.parallelStream().forEach(this.collection::add);
                this.waitingAdd.clear();
            });
//        } else {
//            this.collection.add(obj);
//        }
    }

    public boolean remove(T obj) {
//        if (iterating) {
            waitingRemove.add(obj);
            informTask.submit("EntityTrackingSection: solve_waiting_remove", () -> {
                this.waitingRemove.parallelStream().forEach(this.collection::remove);
                this.waitingRemove.clear();
            });
            return collection.contains(obj);
//        }
//        return
//        return this.collection.remove(obj);
    }

    public void forEach(Predicate<? super T> predicate, Consumer<T> action) {
        collection.parallelStream().filter(predicate).unordered().forEach(action);
    }

    public <U extends T> void forEach(TypeFilter<T, U> type, Predicate<? super U> filter, Consumer<? super U> action) {
        collection.getAllOfType(type.getBaseClass()).stream().forEach(object -> {
            U object2 = type.downcast(object);
            if (object2 != null && filter.test(object2)) {
                action.accept(object2);
            }
        });
    }
}
