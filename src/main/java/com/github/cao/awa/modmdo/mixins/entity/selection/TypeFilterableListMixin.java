package com.github.cao.awa.modmdo.mixins.entity.selection;

import com.google.common.collect.*;
import net.minecraft.util.collection.*;
import org.spongepowered.asm.mixin.*;

import java.util.*;

@Mixin(value = TypeFilterableList.class, priority = 1001)
public class TypeFilterableListMixin<T> {
    @Shadow @Final private Class<T> elementType;

    private final Map<Class<?>, List<T>> elementsByType = Collections.synchronizedMap(Maps.newHashMap());
    private final List<T> allElements = Collections.synchronizedList(Lists.newArrayList());

    /**
     * @author
     */
    @Overwrite
    public boolean add(T e) {
        boolean bl = false;

        for (Map.Entry<Class<?>, List<T>> classListEntry : this.elementsByType.entrySet()) {
            if (classListEntry.getKey().isInstance(e)) {
                bl |= classListEntry.getValue().add(e);
            }
        }

        return bl;
    }

    /**
     * @author
     */
    @Overwrite
    public boolean remove(Object o) {
        boolean bl = false;

        for (Map.Entry<Class<?>, List<T>> entry : this.elementsByType.entrySet()) {
            if ((entry.getKey()).isInstance(o)) {
                List<T> list = entry.getValue();
                bl |= list.remove(o);
            }
        }

        return bl;
    }

    /**
     * @author
     */
    @Overwrite
    public boolean contains(Object o) {
        return this.getAllOfType(o.getClass()).contains(o);
    }

    /**
     * @author
     */
    @SuppressWarnings("unchecked")
    @Overwrite
    public <S> Collection<S> getAllOfType(Class<S> type) {
        Collection<T> collection = this.elementsByType.get(type);

        if (collection == null) {
            collection = this.createAllOfType(type);
        }

        return (Collection<S>) Collections.unmodifiableCollection(collection);
    }

    private <S> Collection<T> createAllOfType(Class<S> type) {
        List<T> list = new ArrayList<>();

        for (T allElement : this.allElements) {
            if (type.isInstance(allElement)) {
                list.add(allElement);
            }
        }

        this.elementsByType.put(type, list);

        return list;
    }

    /**
     * @author
     */
    @Overwrite
    public Iterator<T> iterator() {
        return this.allElements.isEmpty() ? Collections.emptyIterator() : Iterators.unmodifiableIterator(this.allElements.iterator());
    }

    /**
     * @author
     */
    @Overwrite
    public List<T> copy() {
        return ImmutableList.copyOf(this.allElements);
    }

    /**
     * @author
     */
    @Overwrite
    public int size() {
        return this.allElements.size();
    }
}
