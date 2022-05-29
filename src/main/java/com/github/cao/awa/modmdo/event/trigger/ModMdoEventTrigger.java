package com.github.cao.awa.modmdo.event.trigger;

import com.github.cao.awa.modmdo.annotations.*;
import com.github.cao.awa.modmdo.event.*;
import com.github.cao.awa.modmdo.event.trigger.trace.*;
import net.minecraft.server.*;
import org.json.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Auto
public abstract class ModMdoEventTrigger<T extends ModMdoEvent<?>> {
    private TriggerTrace trace;
    private MinecraftServer server;
    private JSONObject meta;

    public JSONObject getMeta() {
        return meta;
    }

    public void setMeta(JSONObject meta) {
        this.meta = meta;
    }

    public MinecraftServer getServer() {
        return server;
    }

    public void setServer(MinecraftServer server) {
        this.server = server;
    }

    public abstract ModMdoEventTrigger<T> build(T event, JSONObject metadata, TriggerTrace triggerTrace);

    public abstract void action();

    public void err(String message, Exception exception) {
        LOGGER.warn(message + at(), exception);
    }

    public String at() {
        return trace.at();
    }

    public TriggerTrace getTrace() {
        return trace;
    }

    public void setTrace(TriggerTrace trace) {
        this.trace = trace;
    }
}
