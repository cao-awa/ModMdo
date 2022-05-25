package com.github.cao.awa.modmdo.event.trigger;

import com.github.cao.awa.modmdo.annotations.*;
import com.github.cao.awa.modmdo.event.*;
import com.github.cao.awa.modmdo.event.trigger.trace.*;
import org.json.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Auto
public abstract class ModMdoEventTrigger<T extends ModMdoEvent<?>> {
    private TriggerTrace trace;

    public abstract ModMdoEventTrigger<T> build(T event, JSONObject metadata, TriggerTrace triggerTrace);

    public abstract void action();

    public String at() {
        return trace.at();
    }

    public void err(String message, Exception exception) {
        LOGGER.warn(message + at(), exception);
    }

    public TriggerTrace getTrace() {
        return trace;
    }

    public void setTrace(TriggerTrace trace) {
        this.trace = trace;
    }
}
