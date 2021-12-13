package com.github.zhuaidadaya.modMdo.usr;

import net.minecraft.server.network.ServerPlayerEntity;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.UUID;

public class UserUtil {
    private final LinkedHashMap<Object, JSONObject> users = new LinkedHashMap<>();

    public UserUtil() {

    }

    public UserUtil(JSONObject json) {
        for(Object o : json.keySet())
            users.put(o.toString(), json.getJSONObject(o.toString()));
    }

    public void put(Object target, JSONObject value) {
        users.put(target, value);
    }

    public JSONObject getJSONObject(Object target) {
        if(users.get(target.toString()) == null)
            throw new IllegalStateException();
        return users.get(target.toString());
    }

    public Object getUserConfig(Object targetUuid, Object getConfig) {
        if(users.get(targetUuid.toString()) == null)
            throw new IllegalStateException();
        return users.get(targetUuid.toString()).get(getConfig.toString()).toString();
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        for(Object o : users.keySet())
            json.put(o.toString(), users.get(o.toString()));
        return json;
    }

    public User[] getUsers() {
        User[] userList = new User[users.size()];
        int i = 0;
        for(Object o : users.keySet()) {
            JSONObject userJSON = users.get(o.toString());
            userList[i++] = new User(userJSON);
        }
        return userList;
    }

    public User getUser(ServerPlayerEntity player) {
        if(users.get(player.getUuid()) == null)
            put(player.getUuid(), new User(player.getName().asString(), player.getUuid()).toJSONObject());
        return new User(users.get(player.getUuid()));
    }

    public User getUser(UUID uuid) {
        return new User(users.get(uuid.toString()));
    }

    public User getUser(String uuid) {
        return new User(users.get(uuid));
    }

    public boolean hasUser(ServerPlayerEntity player) {
        return users.get(player.getUuid().toString()) != null;
    }

    public boolean hasUser(UUID uuid) {
        return users.get(uuid.toString()) != null;
    }

    public boolean hasUser(String uuid) {
        return users.get(uuid) != null;
    }

    public void removeUser(UUID uuid) {
        removeUser(uuid.toString());
    }

    public void removeUser(User user) {
        removeUser(user.getUuid());
    }


    public void removeUser(ServerPlayerEntity player) {
        removeUser(player.getUuid());
    }


    public void removeUser(String uuid) {
        users.remove(uuid);
    }
}
