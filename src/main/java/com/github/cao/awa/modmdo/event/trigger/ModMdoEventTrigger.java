package com.github.cao.awa.modmdo.event.trigger;

import com.github.cao.awa.modmdo.annotations.*;
import com.github.cao.awa.modmdo.event.*;
import com.github.cao.awa.modmdo.event.trigger.trace.*;
import com.github.cao.awa.modmdo.event.variable.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.receptacle.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.server.*;
import com.alibaba.fastjson2.*;

import java.security.*;
import java.util.function.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Auto
public abstract class ModMdoEventTrigger<T extends ModMdoEvent<?>> {
    public static final Object2ObjectArrayMap<String, Consumer<Receptacle<String>>> BASE_FORMATTER = EntrustEnvironment.operation(
            new Object2ObjectArrayMap<>(),
            map -> {
                map.put(
                        "^{variable}",
                        (str) -> {
                            ModMdoPersistent<?> v = SharedVariables.VARIABLES.get(str.getSub())
                                                                             .clone();
                            EntrustEnvironment.trys(() -> v.handle(JSONObject.parseObject(str.get())));
                            str.set(v.get()
                                     .toString());
                        }
                );
                map.put(
                        "^{random}",
                        (str) -> {
                            str.set(String.valueOf(new SecureRandom().nextInt(101)));
                        }
                );
            }
    );
    private TriggerTrace trace;
    private MinecraftServer server;
    private JSONObject meta;
    private boolean enabled;
    private Consumer<T> action;

    public void setAction(Consumer<T> action) {
        this.action = action;
    }

    public Consumer<T> getAction() {
        return action;
    }

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

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ModMdoEventTrigger<T> build(T event, JSONObject metadata, TriggerTrace triggerTrace) {
        setMeta(metadata);
        setTrace(triggerTrace);
        setEnabled(metadata.containsKey("enable") ? metadata.getBoolean("enable") : true);
        return prepare(
                event,
                metadata,
                triggerTrace
        );
    }

    public abstract ModMdoEventTrigger<T> prepare(T event, JSONObject metadata, TriggerTrace triggerTrace);

    public abstract void action();

    public final void ensureAction() {
        if (enabled) {
            action();
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void err(String message, Throwable exception) {
        LOGGER.warn(
                message + at(),
                exception
        );
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
