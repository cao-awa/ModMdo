package com.github.cao.awa.modmdo.security.key;

import com.github.cao.awa.modmdo.annotations.platform.*;
import com.github.cao.awa.modmdo.security.*;
import com.github.cao.awa.modmdo.security.level.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.zhuaidadaya.rikaishinikui.handler.config.encryption.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.receptacle.*;
import it.unimi.dsi.fastutil.objects.*;
import org.jetbrains.annotations.*;
import org.json.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Client
public class SecureKeys extends Storable {
    private final Object2ObjectOpenHashMap<String, SecureKey> keys = new Object2ObjectOpenHashMap<>();
    private SecureLevel level = SecureLevel.UNEQUAL_KEY;

    public SecureLevel getLevel() {
        return level;
    }

    public void setLevel(SecureLevel level) {
        this.level = level;
    }

    public String use(String target, String address) {
        if (keys.containsKey(target)) {
            return use(level, target);
        }
        keep(target, address);
        return use(level, target);
    }

    public void keep(String target, String address) {
        if (level == SecureLevel.UNEQUAL_KEY) {
            set(target, has(target) ? SECURE_KEYS.get(target) : new SecureKey(RandomIdentifier.randomIdentifier(32, true), RandomIdentifier.randomIdentifier(32, true), address));
        } else {
            SecureKey key = has(target) ? SECURE_KEYS.get(target) : new SecureKey(RandomIdentifier.randomIdentifier(32, true), RandomIdentifier.randomIdentifier(32, true), RandomIdentifier.randomIdentifier(), address);
            if (! key.hasId()) {
                key.setId(RandomIdentifier.randomIdentifier());
            }
            set(target, key);
        }
    }

    public void set(String target, SecureKey key) {
        Receptacle<Boolean> create = new Receptacle<>(true);
        for (String s : keys.keySet()) {
            switch (level) {
                case UNEQUAL_KEY -> {
                    if (has(s) && keys.get(s).getPrivateKey().equals(key.getPrivateKey())) {
                        keys.remove(s);
                    }
                }
                case UNEQUAL_ID -> EntrustExecution.notNull(keys.get(s).getId(), id -> {
                    if (id.equals(key.getId())) {
                        keys.remove(s);
                    }
                });
                default -> {
                    return;
                }
            }
        }
        if (create.get()) {
            keys.put(target, key);
        }
    }

    public boolean has(String target) {
        return keys.containsKey(target) && keys.get(target).hasAddress();
    }

    public SecureKey get(String target) {
        return keys.get(target);
    }

    private String use(SecureLevel level, String target) {
        return switch (level) {
            case UNEQUAL_KEY -> EntrustParser.trying(() -> AES.aesEncryptToString(staticConfig.get("identifier").getBytes(), keys.get(target).getPrivateKey().getBytes()), ex -> staticConfig.get("identifier"));
            case UNEQUAL_ID -> keys.get(target).getId();
            default -> staticConfig.get("identifier");
        };
    }

    public boolean hasAddress(@NotNull String address) {
        return keys.keySet().parallelStream().anyMatch(s -> address.equals(keys.get(s).getAddress()));
    }

    public void removeAddress(String address) {
        keys.remove(address);
    }

    public void save() {
        staticConfig.set("private_key", SECURE_KEYS.toJSONObject().toString());
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        EntrustExecution.parallelTryFor(keys.keySet(), target -> json.put(target, keys.get(target).toJSONObject()));
        return json;
    }

    public void load(JSONObject json) {
        EntrustExecution.parallelTryFor(json.keySet(), s -> EntrustExecution.tryTemporary(() -> SECURE_KEYS.set(s, new SecureKey(json.getJSONObject(s)))));
    }
}
