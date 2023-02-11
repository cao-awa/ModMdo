package com.github.cao.awa.modmdo.service.handler.certificate.nosql.lilac.exporter;

import com.alibaba.fastjson2.*;
import com.github.cao.awa.modmdo.security.certificate.*;
import com.github.cao.awa.modmdo.service.handler.certificate.*;
import com.github.cao.awa.modmdo.service.handler.certificate.nosql.*;
import com.github.cao.awa.modmdo.service.handler.certificate.nosql.lilac.*;
import com.github.cao.awa.modmdo.service.upgrader.certificate.*;
import com.github.cao.awa.modmdo.service.upgrader.certificate.request.*;
import com.github.cao.awa.modmdo.utils.io.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.receptacle.*;
import org.apache.logging.log4j.*;

import java.io.*;

/**
 * Export the lilac certificates service.
 *
 * @author 草二号机
 *
 * @since 1.0.43
 */
public class LilacCertificatesExporter extends CertificateServiceExporter {
    private static final Logger LOGGER = LogManager.getLogger("LilacCertificateExporter");

    @Override
    public void exporting(CertificatesUpgradeRequest to) {
        LOGGER.info("Exporting lilac certificate service...");
        Receptacle<Boolean> exported = Receptacle.of(true);

        if (to instanceof LocalCertificatesUpgradeRequest request) {
            EntrustEnvironment.trys(
                    () -> {
                        String path = request.getPath();
                        JSONObject json = new JSONObject();
                        JSONArray certificates = new JSONArray();
                        request.getService()
                               .forEach(certificate -> {
                                   certificates.add(certificate.toJSONObject());

                                   LOGGER.info(
                                           "Handling '{}'",
                                           certificate.getName()
                                   );
                               });
                        json.put(
                                "certificates",
                                certificates
                        );
                        json.put(
                                "service",
                                "lilac"
                        );
                        json.put(
                                "version",
                                LilacCertificateService.SERVICE_DATA_VERSION
                        );

                        LOGGER.info("Writing export...");

                        IOUtil.write(
                                new FileWriter(path),
                                json.toString()
                        );
                    },
                    ex -> {
                        exported.set(false);
                        LOGGER.info(
                                "Lilac certificate service failed exported",
                                ex
                        );
                    }
            );
        } else {
            exported.set(false);
        }

        if (exported.get()) {
            LOGGER.info("Lilac certificate service exported");
        }
    }

    @Override
    public void importing(CertificatesUpgradeRequest from) {
        LOGGER.info("Importing lilac certificate service...");

        Receptacle<Boolean> imported = Receptacle.of(true);

        if (from instanceof LocalCertificatesUpgradeRequest request) {
            EntrustEnvironment.trys(
                    () -> {
                        Certificates<?> certificates = EntrustEnvironment.get(
                                () -> {
                                    Certificates<?> loadingCertificate = new Certificates<>();
                                    JSONObject json = JSONObject.parseObject(EntrustEnvironment.get(
                                            () -> IOUtil.read(new FileReader(request.getPath())),
                                            ""
                                    ));
                                    if (json.containsKey("service") && json.getString("service")
                                                                           .equals("lilac")) {
                                        int version = json.getInteger("version");
                                        if (version == LilacCertificateService.SERVICE_DATA_VERSION) {
                                            JSONArray array = json.getJSONArray("certificates");
                                            for (JSONObject certificateJson : array.toList(JSONObject.class)) {
                                                Certificate certificate = Certificate.build(certificateJson);
                                                loadingCertificate.put(
                                                        certificate.getName(),
                                                        EntrustEnvironment.cast(certificate)
                                                );

                                                LOGGER.info(
                                                        "Handling '{}'",
                                                        certificate.getName()
                                                );
                                            }
                                        } else {
                                            loadingCertificate = CertificatesUpgrader.load(
                                                    version,
                                                    request,
                                                    false
                                            );
                                        }
                                    }
                                    return loadingCertificate;
                                },
                                new Certificates<>()
                        );

                        LocalCertificateService<?> service = request.getService();

                        certificates.forEach(certificate -> service.set(EntrustEnvironment.cast(certificate)));
                    },
                    ex -> {
                        imported.set(false);
                        LOGGER.info(
                                "Lilac certificate service failed import",
                                ex
                        );
                    }
            );
        } else {
            imported.set(false);
        }


        if (imported.get()) {
            LOGGER.info("Lilac certificate service imported");
        }
    }
}
