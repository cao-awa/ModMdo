package com.github.cao.awa.modmdo.event.trigger;

import com.github.cao.awa.modmdo.event.*;
import com.github.cao.awa.modmdo.event.trigger.trace.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.annotaions.*;
import org.json.*;

@SingleThread
public abstract class ModMdoEventTrigger<T extends ModMdoEvent<?>> {
    private TriggerTrace trace;

    public abstract ModMdoEventTrigger<T> build(T event, JSONObject metadata, TriggerTrace triggerTrace);

    public abstract void action();

    public String buildAt() {
        return trace.buildAt();
    }

    public TriggerTrace getTrace() {
        return trace;
    }

    public void setTrace(TriggerTrace trace) {
        this.trace = trace;
    }
}
