package com.github.cao.awa.modmdo.usr;

import com.github.cao.awa.modmdo.storage.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.server.network.*;
import org.json.*;

import java.util.*;

public class UserUtil extends Storable {
    private final Object2ObjectRBTreeMap<String, User> users = new Object2ObjectRBTreeMap<>();
    private final Object2ObjectRBTreeMap<String, String> userNameIdMap = new Object2ObjectRBTreeMap<>();

    public UserUtil() {

    }

    public UserUtil(JSONObject json) {
        for (String o : json.keySet())
            users.put(o, new User(json.getJSONObject(o)));
    }

    public void put(String target, JSONObject value) {
        users.put(target, new User(value));
        try {
            userNameIdMap.put(target, value.getString("uuid"));
        } catch (Exception e) {

        }
    }

    public void put(User user) {
        users.put(user.getUuid().toString(), user);
        userNameIdMap.put(user.getName(), user.getUuid().toString());
    }

    public JSONObject getJSONObject(Object target) {
        if (users.get(target.toString()) == null) throw new IllegalStateException();
        return users.get(target.toString()).toJSONObject();
    }

    public Object getUserConfig(String targetUuid, Object getConfig) {
        if (users.get(targetUuid) == null) throw new IllegalStateException();
        return users.get(targetUuid).toJSONObject().get(getConfig.toString()).toString();
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        for (Object o : users.keySet())
            json.put(o.toString(), users.get(o.toString()));
        return json;
    }

    public User[] getUsers() {
        User[] userList = new User[users.size()];
        int i = 0;
        for (Object o : users.keySet()) {
            JSONObject userJSON = users.get(o.toString()).toJSONObject();
            userList[i++] = new User(userJSON);
        }
        return userList;
    }

    public User getUser(ServerPlayerEntity player) {
        return users.get(player.getUuid().toString());
    }

    public User getUserFromName(String name) {
        return getUser(userNameIdMap.get(name));
    }

    public User getUser(UUID uuid) {
        return users.get(uuid.toString());
    }

    public User getUser(String uuid) {
        return users.get(uuid);
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

    public void setUserLevel(String uuid, int level) {
        users.put(uuid, getUser(uuid).setLevel(level));
    }

    public void setUserLevel(UUID uuid, int level) {
        setUserLevel(uuid.toString(), level);
    }

    public void setUserLevel(ServerPlayerEntity player, int level) {
        setUserLevel(player.getUuid(), level);
    }

    public int getUserLevel(String uuid) {
        return getUser(uuid).getLevel();
    }

    public int getUserLevel(UUID uuid) {
        return getUserLevel(uuid.toString());
    }

    public int getUserLevel(ServerPlayerEntity player) {
        return getUserLevel(player.getUuid());
    }
}
