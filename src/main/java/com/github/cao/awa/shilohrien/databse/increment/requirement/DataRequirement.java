package com.github.cao.awa.shilohrien.databse.increment.requirement;

public abstract class DataRequirement {
    public abstract boolean satisfy(Object t);

    public abstract String name();

    public boolean hasMessage() {
        return false;
    }

    public String getMessage() {
        return "";
    }
}
