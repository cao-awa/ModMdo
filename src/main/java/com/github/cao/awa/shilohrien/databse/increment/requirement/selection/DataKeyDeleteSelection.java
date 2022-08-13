package com.github.cao.awa.shilohrien.databse.increment.requirement.selection;

import com.github.cao.awa.modmdo.annotations.*;
import it.unimi.dsi.fastutil.objects.*;

import java.util.function.*;

@Disposable
public class DataKeyDeleteSelection<T> {
    private final ObjectArrayList<T> selectedDelete = new ObjectArrayList<>();

    public DataKeyDeleteSelection() {

    }

    public void delete(T key) {
        selectedDelete.add(key);
    }

    public void forEach(Consumer<T> action) {
        selectedDelete.forEach(action);
    }
}
