package com.github.cao.awa.modmdo.security.certificate;

import com.github.cao.awa.modmdo.annotations.platform.*;
import com.github.cao.awa.modmdo.server.login.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import org.apache.logging.log4j.*;
import org.jetbrains.annotations.*;
import org.json.*;

@Server
public abstract class Certificate extends Storable {
    private static final Logger LOGGER = LogManager.getLogger("ModMdoCertificate");

    public final @NotNull String name;
    public final @NotNull LoginRecorde recorde;
    public String lastLanguage = "en_us";
    private String type;

    public Certificate(@NotNull String name, @NotNull LoginRecorde recorde) {
        this.name = name;
        this.recorde = recorde;
    }

    public static Certificate build(JSONObject json) {
        return EntrustEnvironment.trys(
                () -> {
                    if (json.getString("type").equals("temporary")) {
                        return TemporaryCertificate.build(json);
                    } else {
                        return PermanentCertificate.build(json);
                    }
                },
                ex -> {
                    LOGGER.debug(
                            "Failed build certificate",
                            ex
                    );
                    return null;
                }
        );
    }

    public String getType() {
        return this.type;
    }

    public Certificate setType(String type) {
        this.type = type;
        return this;
    }

    public String getLastLanguage() {
        return this.lastLanguage;
    }

    public void setLastLanguage(String lastLanguage) {
        this.lastLanguage = lastLanguage;
    }

    public @NotNull String getName() {
        return this.name;
    }

    public String getIdentifier() {
        return this.recorde.getUniqueId();
    }

    public abstract JSONObject toJSONObject();

    public @NotNull LoginRecorde getRecorde() {
        return this.recorde;
    }
}
