package com.github.zhuaidadaya.modmdo.extra.loader;

import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;

import java.util.Map;
import java.util.UUID;

import static com.github.zhuaidadaya.modmdo.storage.Variables.*;

public class ModMdoExtraLoader {
    private final Map<UUID, ModMdoExtra> extras = new Object2ObjectRBTreeMap<>();

    public void register(UUID id, ModMdoExtra extra) {
        extras.put(id, extra);
        LOGGER.info("registered extra: " + id + (extra.hasName() ? "(" + extra.getName() + ")" : ""));
    }

    public void unregister(UUID id) {
        ModMdoExtra extra = extras.get(id);
        extras.remove(id);
        LOGGER.info("unregistered extra: " + id + (extra.hasName() ? "(" + extra.getName() + ")" : ""));
    }

    public void setArg(UUID id, ExtraArgs args) {
        extras.get(id).setArgs(args);
    }

    public void load() {
        for (UUID id : extras.keySet()) {
            ModMdoExtra extra = extras.get(id);
            extra.init();
            LOGGER.info("loaded extra: " + id + (extra.hasName() ? "(" + extra.getName() + ")" : ""));
        }
    }
}
