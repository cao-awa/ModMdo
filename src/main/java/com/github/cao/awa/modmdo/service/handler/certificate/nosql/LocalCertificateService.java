package com.github.cao.awa.modmdo.service.handler.certificate.nosql;

import com.github.cao.awa.modmdo.security.certificate.*;
import com.github.cao.awa.modmdo.service.handler.certificate.*;

/**
 * Use service mode to handle certificates, as ModMdo internal service.
 *
 * @param <T>
 *         Certificate type
 * @author 草二号机
 *
 * @since 1.0.43
 */
public abstract class LocalCertificateService<T extends Certificate> extends CertificateService<T> {
    public abstract String suffix();
}
