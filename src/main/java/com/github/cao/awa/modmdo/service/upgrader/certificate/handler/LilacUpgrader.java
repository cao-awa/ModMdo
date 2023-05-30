package com.github.cao.awa.modmdo.service.upgrader.certificate.handler;

import com.alibaba.fastjson2.*;
import com.github.cao.awa.modmdo.security.certificate.*;
import com.github.cao.awa.modmdo.service.handler.certificate.nosql.lilac.*;
import com.github.cao.awa.modmdo.service.upgrader.certificate.*;
import com.github.cao.awa.modmdo.service.upgrader.certificate.request.*;
import com.github.cao.awa.modmdo.utils.io.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import org.apache.logging.log4j.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.*;

/**
 * Upgrade the certificates. <br>
 * <br>
 * Version after ModMdo v1.0.43, version id is 0.
 *
 * @author 草二号机
 *
 * @since 1.0.43
 */
public final class LilacUpgrader extends CertificatesUpgrader {
    private static final Logger LOGGER = LogManager.getLogger("LilacCertificateUpgrader");

    @Override
    public @Nullable Certificates<Certificate> load(CertificatesUpgradeRequest request, boolean outOfDate) {
        if (request instanceof LocalCertificatesUpgradeRequest local && local.getService() instanceof LilacCertificateService<?> lilac) {
            return EntrustEnvironment.trys(() -> {
                Certificates<Certificate> certificates = new Certificates<>();
                for (File file : Objects.requireNonNull(new File(local.getPath()).listFiles())) {
                    if (file.getName()
                            .endsWith(lilac.suffix())) {
                        Certificate certificate = Certificate.build(JSONObject.parseObject(IOUtil.read(new FileReader(file))));
                        certificates.put(
                                certificate.getName(),
                                certificate
                        );

                        LOGGER.info(
                                outOfDate ? "Out of date, upgrading '{}'" : "Compatible, importing '{}'",
                                certificate.getName()
                        );
                    }
                }
                return certificates;
            });
        }
        return null;
    }

    @Override
    public int version() {
        return 0;
    }
}