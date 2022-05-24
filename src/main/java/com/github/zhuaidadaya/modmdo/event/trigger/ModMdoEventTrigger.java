package com.github.zhuaidadaya.modmdo.event.trigger;

import com.github.zhuaidadaya.modmdo.event.*;
import com.github.zhuaidadaya.modmdo.event.trigger.trace.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.annotaions.*;
import org.json.*;

@SingleThread
public abstract class ModMdoEventTrigger<T extends ModMdoEvent<?>> {
    private TriggerTrace trace;

    public abstract ModMdoEventTrigger<T> build(T event, JSONObject metadata, TriggerTrace triggerTrace);

    public abstract void action();

    public String buildAt() {
        return " <at: " + trace.file().getPath() + ", trigger position: " + trace.position() + "(" + trace.name() + ")>";
    }

    public TriggerTrace getTrace() {
        return trace;
    }

    public void setTrace(TriggerTrace trace) {
        this.trace = trace;
    }
}
