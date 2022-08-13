package com.github.cao.awa.shilohrien.databse.increment.requirement.requires;

import com.github.cao.awa.shilohrien.databse.increment.requirement.*;

import java.util.*;

public class DataRequireEquals extends DataRequirement {
    private final Object obj;

    private DataRequireEquals() {
        this.obj = null;
    }

    public DataRequireEquals(Object objet) {
        this.obj = objet;
    }

    @Override
    public boolean satisfy(Object t) {
        return Objects.equals(obj, t);
    }

    @Override
    public String name() {
        return "equals";
    }
}
