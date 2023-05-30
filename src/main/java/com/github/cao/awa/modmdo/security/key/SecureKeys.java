package com.github.cao.awa.modmdo.security.key;

import com.alibaba.fastjson2.*;
import com.github.cao.awa.modmdo.annotations.platform.*;
import com.github.cao.awa.modmdo.security.*;
import com.github.cao.awa.modmdo.security.level.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.utils.encryption.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import it.unimi.dsi.fastutil.objects.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Client
public class SecureKeys extends Storable {
    private final Map<String, SecureKey> keys = new Object2ObjectOpenHashMap<>();
    private SecureLevel level = SecureLevel.UNEQUAL_KEY;

    public SecureLevel getLevel() {
        return this.level;
    }

    public void setLevel(SecureLevel level) {
        this.level = level;
    }

    public String use(String target, String address) {
        if (this.keys.containsKey(target)) {
            return use(
                    this.level,
                    target
            );
        }
        keep(
                target,
                address
        );
        return use(
                this.level,
                target
        );
    }

    public void keep(String target, String address) {
        SecureKey key;
        if (this.level == SecureLevel.UNEQUAL_KEY) {
            key = has(target) ? SECURE_KEYS.get(target) : new SecureKey(
                    RandomIdentifier.randomIdentifier(32),
                    RandomIdentifier.randomIdentifier(32),
                    address
            );
        } else {
            key = has(target) ? SECURE_KEYS.get(target) : new SecureKey(
                    RandomIdentifier.randomIdentifier(32),
                    RandomIdentifier.randomIdentifier(32),
                    RandomIdentifier.randomIdentifier(4096),
                    address
            );
            if (! key.hasId()) {
                key.setId(RandomIdentifier.randomIdentifier(4096));
            }
        }

        set(
                target,
                key
        );
    }

    public void set(String target, SecureKey key) {
        for (String s : this.keys.keySet()) {
            switch (this.level) {
                case UNEQUAL_KEY -> {
                    if (has(s)) {
                        if (this.keys.get(s)
                                     .getPrivateKey()
                                     .equals(key.getPrivateKey())) {
                            this.keys.remove(s);
                        }
                    }
                }
                case UNEQUAL_ID -> EntrustEnvironment.notNull(
                        this.keys.get(s)
                                 .getId(),
                        id -> {
                            if (id.equals(key.getId())) {
                                this.keys.remove(s);
                            }
                        }
                );
                default -> {
                    return;
                }
            }
        }
        this.keys.put(
                target,
                key
        );
    }

    public boolean has(String target) {
        return this.keys.containsKey(target) && this.keys.get(target)
                                                         .hasAddress();
    }

    public SecureKey get(String target) {
        return this.keys.get(target);
    }

    private String use(SecureLevel level, String target) {
        return switch (level) {
            case UNEQUAL_KEY -> EntrustEnvironment.trys(
                    () -> AES.encryptToString(
                            staticConfig.getString("identifier")
                                        .getBytes(),
                            this.keys.get(target)
                                     .getPrivateKey()
                                     .getBytes()
                    ),
                    () -> staticConfig.getString("identifier")
            );
            case UNEQUAL_ID -> this.keys.get(target)
                                        .getId();
            default -> staticConfig.getString("identifier");
        };
    }

    public boolean hasAddress(@NotNull String address) {
        for (String s : this.keys.keySet()) {
            if (address.equals(this.keys.get(s)
                                        .getAddress())) {
                return true;
            }
        }
        return false;
    }

    public void removeAddress(String address) {
        this.keys.remove(address);
    }

    public void save() {
        staticConfig.set(
                "private_key",
                SECURE_KEYS.toJSONObject()
                           .toString()
        );
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        for (String target : this.keys.keySet()) {
            json.put(
                    target,
                    this.keys.get(target)
                             .toJSONObject()
            );
        }
        return json;
    }

    public void load(JSONObject json) {
        for (String s : json.keySet()) {
            EntrustEnvironment.trys(() -> SECURE_KEYS.set(
                    s,
                    SecureKey.load(json.getJSONObject(s))
            ));
        }
    }
}
