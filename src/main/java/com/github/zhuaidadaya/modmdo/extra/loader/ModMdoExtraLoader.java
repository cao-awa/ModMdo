package com.github.zhuaidadaya.modmdo.extra.loader;

import com.github.zhuaidadaya.modmdo.utils.times.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.activity.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import it.unimi.dsi.fastutil.objects.*;

import java.util.*;
import java.util.concurrent.atomic.*;

import static com.github.zhuaidadaya.modmdo.storage.Variables.*;

public class ModMdoExtraLoader {
    private final Object2ObjectRBTreeMap<UUID, ActivityObject<ModMdoExtra<?>>> extras = new Object2ObjectRBTreeMap<>();
    private final ExtraLoading loading;
    private final ModMdoExtra<?> major;
    private boolean forcedMajor = false;
    private ModMdoExtra<?> loadingExtra = null;
    private UUID loadingId = null;

    public ModMdoExtraLoader(ModMdoExtra<ModMdo> major, String ensure) {
        loading = new ExtraLoading(major);
        this.major = major;
        extras.put(major.getId(), new ActivityObject<>(major, ensure));
        LOGGER.info("Registered major: " + major.getId() + "(" + major.getName() + ")");
    }

    public void register(UUID id, ModMdoExtra<?> extra) {
        EntrustExecution.executeNull(extras.get(id), ActivityObject::active, asNull -> {
            extras.put(id, new ActivityObject<>(extra));
            loading.then(extra);
            LOGGER.info("Registered extra: " + id + "(" + extra.getName() + ")");
        });
    }

    public void register(UUID id, ModMdoExtra<?> extra, String ensure) {
        EntrustExecution.executeNull(extras.get(id), ActivityObject::active, asNull -> {
            extras.put(id, new ActivityObject<>(extra, ensure));
            loading.then(extra);
            LOGGER.info("Registered extra: " + id + "(" + extra.getName() + ")");
        });
    }

    public void unregister(UUID id) {
        ModMdoExtra<?> extra = extras.get(id).invalid().get();
        LOGGER.info("Unregistered extra: " + id + "(" + extra.getName() + ")");
    }

    public void unregister(UUID id, String ensureKey) {
        ModMdoExtra<?> extra = extras.get(id).invalid(ensureKey).get();
        LOGGER.info("Unregistered extra: " + id + "(" + extra.getName() + ")");
    }

    public ModMdoExtra<?> getExtra(UUID id) {
        return extras.get(id).get();
    }

    public ActivityObject<ModMdoExtra<?>> get(UUID id) {
        return extras.get(id);
    }

    public <T> T getExtra(Class<? extends ModMdoExtra<T>> clazz, UUID id) {
        ModMdoExtra<?> extra = extras.get(id).get();
        if (extra.getClass().equals(clazz)) {
            return (T) extra;
        }
        return null;
    }

    public ModMdo getModMdo() {
        return (ModMdo) major;
    }

    public boolean isActive(UUID id) {
        return EntrustParser.trying(() -> extras.get(id).isActive(), () -> false);
    }

    public void setArg(UUID id, UncertainParameter args) {
        extras.get(id).get().setArgs(args);
    }

    public void load() {
        AtomicBoolean loaded = new AtomicBoolean(false);
        AtomicLong start = new AtomicLong(TimeUtil.millions());
        new Thread(() -> {
            while (! loaded.get()) {
                long time = TimeUtil.processMillion(start.get());
                if ((time - 320) % 1000 == 0) {
                    if (loadingExtra != null) {
                        LOGGER.warn("Extra: " + loadingId + "(" + loadingExtra.getName() + ") loading time has " + time / 1000 + " seconds longer than expected");
                    }
                }

                EntrustExecution.tryTemporary(() -> TimeUtil.barricade(10));
            }
        }).start();

        loading.load(extra -> {
            boolean active = extras.get(extra.getId()).isActive();
            long startLoad = TimeUtil.millions();
            loadingId = extra.getId();
            loadingExtra = extra;
            LOGGER.info("Loading extra: " + loadingId + "(" + loadingExtra.getName() + ")");
            loadingExtra.auto(forcedMajor || active);
            LOGGER.info("Loaded extra: " + loadingId + "(" + loadingExtra.getName() + ") in " + TimeUtil.processMillion(startLoad) + "ms");
            start.set(TimeUtil.millions());
        }, failed -> {
            LOGGER.info("Failed to load extra: " + loadingId + "(" + loadingExtra.getName() + ")");
            start.set(TimeUtil.millions());
        });

        loadingExtra = null;
        loadingId = null;

        loaded.set(true);
    }

    public void force() {
        forcedMajor = true;
    }
}
