package com.github.cao.awa.modmdo.service.handler.certificate;

import com.github.cao.awa.modmdo.security.certificate.*;
import com.github.cao.awa.modmdo.service.upgrader.certificate.request.*;
import org.jetbrains.annotations.*;

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
public abstract class CertificateService<T extends Certificate> {
    public abstract boolean containsName(@NotNull String name);

    public abstract boolean containsIdentifier(@NotNull String id);

    public abstract T get(@NotNull String name);

    public abstract T getFromId(@NotNull String id);

    public abstract void set(@NotNull String name, @Nullable T certificate);

    public abstract void delete(@NotNull String name);

    public abstract void clear();

    public abstract int count();

    public abstract void forEach(@NotNull Consumer<T> action);

    public abstract Collection<String> keys();

    public abstract Collection<T> values();

    public abstract boolean verifyUUID(@NotNull String name, @NotNull String uuid);

    public void set(@NotNull T certificate) {
        this.set(
                certificate.getName(),
                certificate
        );
    }

    public void online() {

    }

    public void offline() {

    }

    public void upgrade() {

    }

    public void exporting(CertificatesUpgradeRequest to) {

    }

    public void importing(CertificatesUpgradeRequest from) {

    }
}
