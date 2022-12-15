package com.github.cao.awa.modmdo.service.upgrader.certificate.request;

import com.github.cao.awa.modmdo.service.handler.certificate.nosql.*;

/**
 * Request to upgrader, use local mode.
 *
 * @author 草二号机
 */
public class LocalCertificatesUpgradeRequest extends CertificatesUpgradeRequest {
    private final String path;
    private final LocalCertificateService<?> service;

    public LocalCertificatesUpgradeRequest(String path, LocalCertificateService<?> service) {
        this.path = path;
        this.service = service;
    }

    public LocalCertificateService<?> getService() {
        return service;
    }

    public String getPath() {
        return this.path;
    }
}
