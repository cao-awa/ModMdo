package com.github.cao.awa.modmdo.service.upgrader.certificate;

import com.github.cao.awa.modmdo.security.certificate.*;
import com.github.cao.awa.modmdo.service.upgrader.certificate.handler.*;
import com.github.cao.awa.modmdo.service.upgrader.certificate.request.*;
import it.unimi.dsi.fastutil.ints.*;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 * Upgrade the certificates.
 *
 * @author 草二号机
 * @since 1.0.43
 */
public abstract class CertificatesUpgrader {
    private static final Map<Integer, CertificatesUpgrader> INSTANCES = new Int2ObjectOpenHashMap<>();

    public static void register(CertificatesUpgrader upgrader) {
        INSTANCES.put(
                upgrader.version(),
                upgrader
        );
    }

    public static void init() {
        register(new OldestUpgrader());
        register(new LilacUpgrader());
    }

    public abstract int version();

    public static @Nullable Certificates<Certificate> load(int version, CertificatesUpgradeRequest request, boolean outOfData) {
        CertificatesUpgrader upgrader = INSTANCES.get(version);
        if (upgrader == null) {
            return null;
        }
        return upgrader.load(
                request,
                outOfData
        );
    }

    public abstract @Nullable Certificates<Certificate> load(CertificatesUpgradeRequest request, boolean outOfDate);
}
