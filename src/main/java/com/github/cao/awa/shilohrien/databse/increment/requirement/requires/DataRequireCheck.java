package com.github.cao.awa.shilohrien.databse.increment.requirement.requires;

import com.github.cao.awa.shilohrien.databse.increment.requirement.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.*;

public class DataRequireCheck extends DataRequirement {
    private final Action<Object, Boolean> obj;

    private DataRequireCheck() {
        this.obj = t -> true;
    }

    public DataRequireCheck(Action<Object, Boolean> objet) {
        this.obj = objet;
    }

    @Override
    public boolean satisfy(Object t) {
        return obj.action(t);
    }

    @Override
    public String name() {
        return "check";
    }
}
