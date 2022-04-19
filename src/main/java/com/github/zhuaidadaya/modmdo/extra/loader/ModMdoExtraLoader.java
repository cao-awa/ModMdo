package com.github.zhuaidadaya.modmdo.extra.loader;

import com.github.zhuaidadaya.modmdo.utils.times.TimeUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static com.github.zhuaidadaya.modmdo.storage.Variables.*;

public class ModMdoExtraLoader {
    private final Map<UUID, ModMdoExtra> extras = new Object2ObjectRBTreeMap<>();
    private ModMdoExtra loadingExtra = null;
    private UUID loadingId = null;

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
        AtomicBoolean loaded = new AtomicBoolean(false);
        AtomicLong start = new AtomicLong(TimeUtil.millions());
        new Thread(() -> {
            while (! loaded.get()) {
                long time = TimeUtil.processMillion(start.get());
                if ((time - 320) % 1000 == 0) {
                    if (loadingExtra != null) {
                        LOGGER.warn("extra: " + loadingId + (loadingExtra.hasName() ? "(" + loadingExtra.getName() + ")" : "") + " loading time has " + time / 1000 + " seconds longer than expected");
                    }
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {

                }
            }
        }).start();
        for (UUID id : extras.keySet()) {
            long startLoad = TimeUtil.millions();
            loadingId = id;
            loadingExtra = extras.get(id);
            LOGGER.info("loading extra: " + id + (loadingExtra.hasName() ? "(" + loadingExtra.getName() + ")" : ""));
            loadingExtra.init();
            LOGGER.info("loaded extra: " + id + (loadingExtra.hasName() ? "(" + loadingExtra.getName() + ")" : "") + " in " + TimeUtil.processMillion(startLoad) + "ms");
            start.set(TimeUtil.millions());
        }
        loadingExtra = null;
        loadingId = null;
        loaded.set(true);
    }
}
