package com.github.cao.awa.modmdo.service.upgrader.certificate.handler;

import com.alibaba.fastjson2.*;
import com.github.cao.awa.modmdo.security.certificate.*;
import com.github.cao.awa.modmdo.service.upgrader.certificate.*;
import com.github.cao.awa.modmdo.service.upgrader.certificate.request.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import org.apache.logging.log4j.*;
import org.jetbrains.annotations.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

/**
 * Upgrade the certificates. <br>
 * <br>
 * Version before ModMdo v1.0.43, version id is -1.
 *
 * @author 草二号机
 *
 * @since 1.0.43
 */
public final class OldestUpgrader extends CertificatesUpgrader {
    private static final Logger LOGGER = LogManager.getLogger("OldestCertificateUpgrader");

    @Override
    public @Nullable Certificates<Certificate> load(CertificatesUpgradeRequest request, boolean outOfDate) {
        if (request instanceof LocalCertificatesUpgradeRequest local) {
            return EntrustEnvironment.trys(() -> {
                Certificates<Certificate> certificates = new Certificates<>();
                JSONObject json = config.getJSONObject("whitelists");
                for (String key : json.keySet()) {
                    certificates.put(
                            key,
                            Certificate.build(json.getJSONObject(key))
                    );

                    LOGGER.info(
                            "Out of date, upgrading '{}'",
                            key
                    );
                }
                return certificates;
            });
        }
        return null;
    }

    @Override
    public int version() {
        return - 1;
    }
}
