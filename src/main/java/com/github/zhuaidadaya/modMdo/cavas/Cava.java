package com.github.zhuaidadaya.modMdo.cavas;

import com.github.zhuaidadaya.MCH.times.TimeType;
import com.github.zhuaidadaya.MCH.times.Times;
import com.github.zhuaidadaya.modMdo.usr.User;
import org.json.JSONObject;

import java.util.UUID;

public class Cava {
    private final User creatorUser;
    private final String message;
    private final String createTime;
    private final String id;

    public Cava(User creatorUser, String message) {
        this.creatorUser = creatorUser;
        this.message = message;
        this.createTime = Times.getTime(TimeType.AS_SECOND);
        this.id = UUID.randomUUID().toString();
    }

    public Cava(JSONObject fromJson) {
        this.createTime = fromJson.get("createTime").toString();
        this.creatorUser = new User(fromJson.getJSONObject("creator"));
        this.message = fromJson.get("message").toString();
        this.id = fromJson.get("id").toString();
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        json.put("createTime", createTime);
        json.put("creator", creatorUser.toJSONObject());
        json.put("message", message);
        json.put("id", id);
        return json;
    }

    public String getID() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public User getCreator() {
        return creatorUser;
    }

    public String getCreateTime() {
        return createTime;
    }
}
