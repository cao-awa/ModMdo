package com.github.cao.awa.shilohrien.databse.increment.requirement.requires;


import com.github.cao.awa.shilohrien.databse.increment.requirement.*;

public class DataRequireNotNull extends DataRequirement {
    public DataRequireNotNull() {

    }

    @Override
    public boolean satisfy(Object t) {
        return t != null;
    }

    @Override
    public String name() {
        return "not_null";
    }
}
