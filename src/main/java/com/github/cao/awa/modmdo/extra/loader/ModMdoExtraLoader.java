package com.github.cao.awa.modmdo.extra.loader;

import com.github.cao.awa.modmdo.annotations.extra.*;
import com.github.cao.awa.modmdo.utils.times.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.activity.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import it.unimi.dsi.fastutil.objects.*;
import org.apache.logging.log4j.*;

import java.util.*;
import java.util.concurrent.atomic.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

public class ModMdoExtraLoader {
    private static final Logger LOGGER = LogManager.getLogger("ModMdoExtraLoader");
    private final Object2ObjectRBTreeMap<UUID, ActivityObject<ModMdoExtra<?>>> extras = new Object2ObjectRBTreeMap<>();
    private final ExtraLoading loading;
    private final ModMdoExtra<?> major;
    private final boolean forcedMajor = false;
    private ModMdoExtra<?> loadingExtra = null;
    private UUID loadingId = null;

    public ModMdoExtraLoader(ModMdoExtra<ModMdo> major) {
        loading = new ExtraLoading(major);
        this.major = major;
        extras.put(major.getId(), new ActivityObject<>(major));
        LOGGER.info("Registered major: " + major.getId() + "(" + major.getName() + ")");
    }

    public void unregister(UUID id) {
        ModMdoExtra<?> extra = extras.get(id).invalid().get();
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
        return EntrustEnvironment.trys(() -> extras.get(id).isActive(), () -> false);
    }

    public void load() {
        extras.forEach((k, v) -> {
            if (v.get().isSignAuto()) {
                extras.remove(k);
            }
        });

        for (Class<?> clazz : EXTRA_AUTO.getTypesAnnotatedWith(ModMdoAutoExtra.class)) {
            EntrustEnvironment.trys(() -> {
                LOGGER.info("Registering for auto extra: " + clazz.getName());
                ModMdoExtra<?> extra = (ModMdoExtra<?>) clazz.getDeclaredConstructor().newInstance();
                extra.prepare();
                extra.signAuto();
                register(extra.getId(), extra);
            }, ex -> {
                LOGGER.debug("Failed to register auto extra: " + clazz.getName(), ex);
            });
        }

        LOGGER.info("Loading ModMdo extras");

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

                EntrustEnvironment.trys(() -> TimeUtil.coma(10));
            }
        }).start();

        loading.load(extra -> {
            if (!extra.isSignAuto()) {
                extra.prepare();
            }
            boolean active = extras.get(extra.getId()).isActive();
            long startLoad = TimeUtil.millions();
            loadingId = extra.getId();
            loadingExtra = extra;
            LOGGER.info("Loading extra: " + loadingId + "(" + loadingExtra.getName() + ")");
            loadingExtra.auto(forcedMajor || active);
            LOGGER.info("Loaded extra: " + loadingId + "(" + loadingExtra.getName() + ") in " + TimeUtil.processMillion(startLoad) + "ms");
            start.set(TimeUtil.millions());
        }, failed -> {
            LOGGER.debug("Failed to load extra: " + failed.getId() + "(" + failed.getName() + ")");
            start.set(TimeUtil.millions());
        });

        loadingExtra = null;
        loadingId = null;

        loaded.set(true);
    }

    public void register(UUID id, ModMdoExtra<?> extra) {
        EntrustEnvironment.nulls(extras.get(id), ActivityObject::active, asNull -> {
            extras.put(id, new ActivityObject<>(extra));
            loading.then(extra);
            LOGGER.info("Registered extra: " + id + "(" + extra.getName() + ")");
        });
    }
}
