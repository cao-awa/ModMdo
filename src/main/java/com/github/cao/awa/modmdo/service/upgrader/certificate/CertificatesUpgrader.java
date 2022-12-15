package com.github.cao.awa.modmdo.service.upgrader.certificate;

import com.github.cao.awa.modmdo.security.certificate.*;
import com.github.cao.awa.modmdo.service.upgrader.certificate.handler.*;
import com.github.cao.awa.modmdo.service.upgrader.certificate.request.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import it.unimi.dsi.fastutil.ints.*;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 * Upgrade the certificates.
 *
 * @author 草二号机
 */
public abstract class CertificatesUpgrader {
    private static final Map<Integer, CertificatesUpgrader> INSTANCES = EntrustEnvironment.operation(
            new Int2ObjectOpenHashMap<>(),
            map -> {
                map.put(
                        - 1,
                        new OldestUpgrader()
                );

                map.put(
                        0,
                        new LilacUpgrader()
                );
            }
    );

    public static @Nullable Certificates<Certificate> load(int version, CertificatesUpgradeRequest request, boolean outOfData) {
        CertificatesUpgrader upgrader = INSTANCES.get(version);
        if (upgrader == null) {
            return null;
        }
        return upgrader.load(request,
                             outOfData);
    }

    public abstract @Nullable Certificates<Certificate> load(CertificatesUpgradeRequest request, boolean outOfDate);

    public abstract int version();
}
