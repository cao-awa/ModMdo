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
import org.json.*;

import java.security.*;
import java.util.function.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Auto
public abstract class ModMdoEventTrigger<T extends ModMdoEvent<?>> {
    public static final Object2ObjectArrayMap<String, BiConsumer<ModMdoEventTrigger<?>, Receptacle<String>>> BASE_FORMATTER = EntrustParser.operation(new Object2ObjectArrayMap<>(), map -> {
        map.put("^{variable}", (trigger, str) -> {
            ModMdoPersistent<?> v = SharedVariables.variables.get(str.getSub()).clone();
            EntrustExecution.tryTemporary(() -> {
                v.handle(new JSONObject(str.get()));
            });
            str.set(v.get().toString());
        });
        map.put("^{random}", (trigger, str) -> {
            str.set(String.valueOf(new SecureRandom().nextInt(101)));
        });
    });
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
