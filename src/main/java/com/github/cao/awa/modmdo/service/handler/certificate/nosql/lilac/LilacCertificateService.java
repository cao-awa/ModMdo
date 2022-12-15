package com.github.cao.awa.modmdo.service.handler.certificate.nosql.lilac;

import com.github.cao.awa.modmdo.math.coded.base.*;
import com.github.cao.awa.modmdo.security.certificate.*;
import com.github.cao.awa.modmdo.service.handler.certificate.nosql.*;
import com.github.cao.awa.modmdo.service.handler.certificate.nosql.lilac.exporter.*;
import com.github.cao.awa.modmdo.service.upgrader.certificate.*;
import com.github.cao.awa.modmdo.service.upgrader.certificate.request.*;
import com.github.cao.awa.modmdo.utils.file.*;
import com.github.cao.awa.modmdo.utils.io.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.receptacle.*;
import org.apache.logging.log4j.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.*;
import java.util.function.*;

/**
 * Use service mode to handle certificates.
 *
 * @param <T>
 *         Certificate type
 * @author 草二号机
 * @since 1.0.43
 */
public class LilacCertificateService<T extends Certificate> extends LocalCertificateService<T> {
    public static final int SERVICE_DATA_VERSION = 0;
    private static final Logger LOGGER = LogManager.getLogger("LilacCertificateService");
    private static final LilacCertificatesExporter EXPORTER = new LilacCertificatesExporter();
    private final Certificates<T> certificates = new Certificates<>();
    private final @Nullable String path;

    public LilacCertificateService(@Nullable String path) {
        this.path = path;
        online();
    }

    @Override
    public boolean containsName(@NotNull String name) {
        return this.certificates.containsName(name);
    }

    @Override
    public boolean containsIdentifier(@NotNull String id) {
        return this.certificates.containsIdentifier(id);
    }

    @Override
    public T get(@NotNull String name) {
        return this.certificates.get(name);
    }

    @Override
    public T getFromId(@NotNull String id) {
        return this.certificates.getFromId(id);
    }

    @Override
    public void set(@NotNull String name, @Nullable T certificate) {
        if (certificate == null) {
            this.certificates.remove(name);
        } else {
            this.certificates.put(
                    name,
                    certificate
            );
        }

        if (path == null) {
            return;
        }

        File file = new File(path + "/" + name + "." + suffix());

        if (certificate == null) {
            FileUtil.delete(file);
        } else {
            FileUtil.mkdirsParent(file);
            EntrustEnvironment.trys(() -> IOUtil.write(
                    new BufferedWriter(new FileWriter(file)),
                    certificate.toJSONObject()
                               .toString()
            ));
        }
    }

    @Override
    public void delete(@NotNull String name) {
        set(
                name,
                null
        );
    }

    @Override
    public void clear() {
        this.certificates.clear();
    }

    @Override
    public int count() {
        return certificates.size();
    }

    @Override
    public void forEach(@NotNull Consumer<T> action) {
        this.certificates.forEach(action);
    }

    @Override
    public Collection<String> keys() {
        return this.certificates.keySet();
    }

    @Override
    public Collection<T> values() {
        return this.certificates.values();
    }

    public boolean verifyUUID(@NotNull String name, @NotNull String uuid) {
        return EntrustEnvironment.get(
                () -> uuid.equals(this.get(name)
                                      .getRecorde()
                                      .getUuid()
                                      .toString()),
                false
        );
    }

    @Override
    public void online() {
        LOGGER.info("Loading lilac certificate service ...");
        upgrade();
        EntrustEnvironment.trys(() -> {
            if (this.path == null) {
                return;
            }
            IOUtil.write(
                    new FileOutputStream(path + "/VERSION.ver"),
                    Base256.intToBuf(SERVICE_DATA_VERSION)
            );
        });
        LOGGER.info("Lilac certificate service ready");
    }

    public void upgrade() {
        int version = EntrustEnvironment.get(
                () -> Base256.intFromBuf(IOUtil.readBytes(new FileReader(path + "/VERSION.ver"))),
                - 1
        );
        LOGGER.info("Upgrading lilac certificate service ...");

        Receptacle<Boolean> upgraded = Receptacle.of(true);

        this.certificates.clear();
        EntrustEnvironment.notNull(
                CertificatesUpgrader.load(
                        version,
                        new LocalCertificatesUpgradeRequest(
                                path,
                                this
                        ),
                        version != SERVICE_DATA_VERSION
                ),
                certificates -> EntrustEnvironment.trys(
                        () -> certificates.forEach(certificate -> this.set(EntrustEnvironment.cast(certificate))),
                        () -> upgraded.set(false)
                )
        );

        if (upgraded.get()) {
            LOGGER.info("Lilac certificate service upgraded");
        } else {
            LOGGER.info("Lilac certificate service failed upgrade, discarded");
        }
    }

    @Override
    public void exporting(CertificatesUpgradeRequest to) {
        EXPORTER.exporting(to);
    }

    @Override
    public void importing(CertificatesUpgradeRequest from) {
        EXPORTER.importing(from);
    }

    @Override
    public String suffix() {
        return "certificate";
    }
}
