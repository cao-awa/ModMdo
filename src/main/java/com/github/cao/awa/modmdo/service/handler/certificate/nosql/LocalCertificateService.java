package com.github.cao.awa.modmdo.service.handler.certificate.nosql;

import com.github.cao.awa.modmdo.security.certificate.*;
import com.github.cao.awa.modmdo.service.handler.certificate.*;

public abstract class LocalCertificateService<T extends Certificate> extends CertificateService<T> {
    public abstract String suffix();
}
