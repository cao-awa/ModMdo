package com.github.cao.awa.shilohrien.databse.increment.requirement.requires;

import com.github.cao.awa.shilohrien.databse.increment.requirement.*;

public class DataRequireInstanceOf extends DataRequirement {
    private final Class<?> clazz;

    private DataRequireInstanceOf() {
        this.clazz = null;
    }

    public DataRequireInstanceOf(Class<?> clazz) {
        this.clazz = clazz;
    }

    public DataRequireInstanceOf(Object instance) {
        this.clazz = instance.getClass();
    }

    @Override
    public boolean satisfy(Object t) {
        return clazz.isInstance(t);
    }

    @Override
    public String name() {
        return "instance_of";
    }

    public boolean hasMessage() {
        return true;
    }

    public String getMessage() {
        return clazz.toString();
    }
}
