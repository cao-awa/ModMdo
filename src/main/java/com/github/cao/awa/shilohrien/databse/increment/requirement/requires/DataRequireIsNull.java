package com.github.cao.awa.shilohrien.databse.increment.requirement.requires;


import com.github.cao.awa.shilohrien.databse.increment.requirement.*;

public class DataRequireIsNull extends DataRequirement {
    public DataRequireIsNull() {

    }

    @Override
    public boolean satisfy(Object t) {
        return t == null;
    }

    @Override
    public String name() {
        return "is_null";
    }
}
