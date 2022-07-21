package com.github.cao.awa.modmdo.usr;

import com.github.cao.awa.modmdo.lang.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.utils.times.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import net.minecraft.text.*;
import org.json.*;

import java.util.*;

public class User extends Storable {
    private final long loginTime = TimeUtil.millions();
    private String name;
    private UUID uuid;
    private int level = 1;
    private String modmdoIdentifier = "";
    private int modmdoVersion;
    private String modmdoName;
    private Language language = SharedVariables.getLanguage();
    private Text message = null;

    public User() {
    }

    public User(String name) {
        this.name = name;
    }

    public User(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    public User(String name, String uuid) {
        this.name = name;
        this.uuid = UUID.fromString(uuid);
    }

    public User(String name, String uuid, int level) {
        this.name = name;
        this.uuid = UUID.fromString(uuid);
        this.level = level;
    }

    public User(String name, String uuid, int level, String modmdoIdentifier) {
        this.name = name;
        this.uuid = UUID.fromString(uuid);
        this.level = level;
        this.modmdoIdentifier = modmdoIdentifier;
    }

    public User(String name, String uuid, int level, String modmdoIdentifier, int modmdoVersion) {
        this.name = name;
        this.uuid = UUID.fromString(uuid);
        this.level = level;
        this.modmdoIdentifier = modmdoIdentifier;
        this.modmdoVersion = modmdoVersion;
    }

    public User(JSONObject json) {
        String name = json.get("name").toString();
        String uuid = json.get("uuid").toString();
        int level = json.getInt("level");

        this.name = name;
        this.uuid = UUID.fromString(uuid);
        this.level = level;

        modmdoVersion = EntrustParser.tryCreate(() -> json.getInt("version"), - 1);

        modmdoIdentifier = EntrustParser.tryCreate(() -> json.getString("identifier"), "");
    }

    public String getModmdoName() {
        return modmdoName;
    }

    public User setModmdoName(String modmdoName) {
        this.modmdoName = modmdoName;
        return this;
    }

    public long getLoginTime() {
        return loginTime;
    }

    public Text getMessage() {
        return message;
    }

    public User setMessage(Text message) {
        this.message = message;
        return this;
    }

    public Language getLanguage() {
        return language;
    }

    public User setLanguage(Language language) {
        this.language = language;
        return this;
    }

    public String getIdentifier() {
        return modmdoIdentifier;
    }

    public User setIdentifier(String modmdoIdentifier) {
        this.modmdoIdentifier = modmdoIdentifier;
        return this;
    }

    public int getVersion() {
        return modmdoVersion;
    }

    public User setVersion(int version) {
        this.modmdoVersion = version;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("uuid", getUuid().toString());
        json.put("level", level);
        json.put("version", modmdoVersion);
        json.put("identifier", modmdoIdentifier);

        return json;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = UUID.fromString(uuid);
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public int getLevel() {
        return level;
    }

    public User setLevel(int level) {
        this.level = level;
        return this;
    }
}
