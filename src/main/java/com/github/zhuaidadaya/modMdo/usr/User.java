package com.github.zhuaidadaya.modMdo.usr;

import com.github.zhuaidadaya.modMdo.login.token.ClientEncryptionToken;
import it.unimi.dsi.fastutil.objects.ObjectRBTreeSet;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.UUID;

public class User {
    private String name;
    private UUID uuid;
    private int level = 1;
    private long onlineTime = 0;
    private ClientEncryptionToken clientToken = null;
    private ObjectRBTreeSet<String> follows = new ObjectRBTreeSet<>();
    private boolean dummyPlayer = true;

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

    public User(String name, String uuid, int level, ClientEncryptionToken token) {
        this.name = name;
        this.uuid = UUID.fromString(uuid);
        this.level = level;
        this.clientToken = token;
        this.dummyPlayer = false;
    }

    public User(JSONObject json) {
        String name = json.get("name").toString();
        String uuid = json.get("uuid").toString();
        int level = json.getInt("level");

        this.name = name;
        this.uuid = UUID.fromString(uuid);
        this.level = level;

        try {
            JSONObject token = json.getJSONObject("token");
            for(Object o : token.keySet()) {
                JSONObject tokenContent = token.getJSONObject(o.toString());
                this.clientToken = new ClientEncryptionToken(tokenContent.getString("token"), tokenContent.getString("address"), tokenContent.getString("login_type"), tokenContent.getString("modmdo_version"));
            }
            dummyPlayer = false;
        } catch (Exception e) {

        }

        try {
            JSONArray subs = json.getJSONArray("subs");
            for(Object o : subs)
                this.follows.add(o.toString());
        } catch (Exception e) {

        }

        try {
            onlineTime = json.getLong("onlineTime");
        } catch (Exception e) {

        }

        try {
            if(dummyPlayer)
                dummyPlayer = json.getBoolean("dummy");
        } catch (Exception e) {

        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getID() {
        return uuid.toString();
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        try {
            json.put("dummy", dummyPlayer);
            json.put("onlineTime", onlineTime);
            json.put("name", name);
            json.put("uuid", uuid);
            json.put("level", level);
            json.put("token", clientToken.toJSONObject());
            json.put("subs", new JSONArray(follows.toArray()));
        } catch (Exception e) {
            json.put("dummy", dummyPlayer);
            json.put("onlineTime", onlineTime);
            json.put("name", name);
            json.put("uuid", uuid);
            json.put("level", level);
            json.put("subs", new JSONArray(follows.toArray()));
        }

        return json;
    }

    public boolean isDummyPlayer() {
        return dummyPlayer;
    }

    public int getLevel() {
        return level;
    }

    public User setLevel(int level) {
        this.level = level;
        return this;
    }

    public ClientEncryptionToken getClientToken() {
        return clientToken;
    }

    public User setClientToken(ClientEncryptionToken token) {
        this.clientToken = token;
        this.dummyPlayer = false;
        return this;
    }

    public User addFollows(String... follows) {
        if(follows != null)
            this.follows.addAll(List.of(follows));
        return this;
    }

    public ObjectRBTreeSet<String> getFollows() {
        return follows;
    }

    public void removeFollow(String follow) {
        this.follows.remove(follow);
    }

    public void clearFollows() {
        this.follows = new ObjectRBTreeSet<>();
    }

    public boolean isFollow(String... follows) {
        return this.follows.containsAll(List.of(follows));
    }

    public void addOnlineTime(long timeMillion) {
        onlineTime += timeMillion;
    }

    public long processRemainingSeconds() {
        long onlineSeconds = getOnlineTime() / 1000;

        onlineSeconds -= (onlineSeconds > 59 ? onlineSeconds / 60 : 0) * 60;
        return onlineSeconds;
    }

    public long processRemainingMinutes() {
        long onlineSeconds = getOnlineTime() / 1000;

        long onlineMinutes = onlineSeconds / 60;
        onlineMinutes -= (onlineMinutes > 59 ? onlineMinutes / 60 : 0) * 60;
        return onlineMinutes;
    }

    public long processRemainingHours() {
        long onlineSeconds = getOnlineTime() / 1000;

        long onlineMinutes = onlineSeconds / 60;
        long onlineHours = onlineMinutes > 59 ? onlineMinutes / 60 : 0;
        onlineHours -= (onlineHours > 23 ? onlineHours / 24 : 0) * 24;
        return onlineHours;
    }

    public long processRemainingDays() {
        long onlineSeconds = getOnlineTime() / 1000;

        long onlineMinutes = onlineSeconds / 60;
        long onlineHours = onlineMinutes > 59 ? onlineMinutes / 60 : 0;
        long onlineDays = onlineHours > 23 ? onlineHours / 24 : 0;
        onlineDays -= (onlineDays > 29 ? onlineDays / 30 : 0) * 30;
        return onlineDays;
    }

    public long processRemainingMonths() {
        long onlineSeconds = getOnlineTime() / 1000;

        long onlineMinutes = onlineSeconds / 60;
        long onlineHours = onlineMinutes > 59 ? onlineMinutes / 60 : 0;
        long onlineDays = onlineHours > 23 ? onlineHours / 24 : 0;
        long onlineMonths = onlineDays > 29 ? onlineDays / 30 : 0;
        onlineMonths -= (onlineMonths > 11 ? onlineMonths / 12 : 0) * 12;
        return onlineMonths;
    }

    public long processRemainingYears() {
        long onlineSeconds = getOnlineTime() / 1000;

        long onlineMinutes = onlineSeconds / 60;
        long onlineHours = onlineMinutes > 59 ? onlineMinutes / 60 : 0;
        long onlineDays = onlineHours > 23 ? onlineHours / 24 : 0;
        long onlineMonths = onlineDays > 29 ? onlineDays / 30 : 0;
        return onlineMonths > 11 ? onlineMonths / 12 : 0;
    }

    public long getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(long onlineTime) {
        this.onlineTime = onlineTime;
    }

    public long formatOnlineSecond() {
        return onlineTime / 1000;
    }

    public long formatOnlineMinute() {
        if(getOnlineSecond() > 59)
            return getOnlineSecond() / 60;
        else
            return 0;
    }

    public long formatOnlineHour() {
        if(getOnlineMinute() > 59)
            return getOnlineMinute() / 60;
        else
            return 0;
    }

    public long formatOnlineDay() {
        if(getOnlineHour() > 23)
            return getOnlineHour() / 24;
        else
            return 0;
    }

    public long formatOnlineMonth() {
        if(getOnlineDay() > 29)
            return getOnlineDay() / 30;
        else
            return 0;
    }

    public long getOnlineSecond() {
        return formatOnlineSecond();
    }

    public long getOnlineMinute() {
        if(getOnlineSecond() > 59)
            return formatOnlineMinute();
        else
            return 0;
    }

    public long getOnlineHour() {
        if(getOnlineMinute() > 59)
            return formatOnlineHour();
        else
            return 0;
    }

    public long getOnlineDay() {
        if(getOnlineHour() > 23)
            return formatOnlineDay();
        else
            return 0;
    }

    public long getOnlineMonth() {
        if(getOnlineSecond() > 29)
            return formatOnlineMonth();
        else
            return 0;
    }
}
