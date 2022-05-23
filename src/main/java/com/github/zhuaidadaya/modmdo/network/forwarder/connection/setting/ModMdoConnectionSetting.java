package com.github.zhuaidadaya.modmdo.network.forwarder.connection.setting;

import com.github.zhuaidadaya.modmdo.storage.*;
import org.json.*;

import static com.github.zhuaidadaya.modmdo.storage.SharedVariables.configCached;

public class ModMdoConnectionSetting {
    private boolean chat = true;
    private boolean playerJoin = true;
    private boolean playerQuit = true;
    private boolean testing = false;

    public boolean isPlayerQuit() {
        return playerQuit;
    }

    public void setPlayerQuit(boolean playerQuit) {
        this.playerQuit = playerQuit;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChat() {
        return chat;
    }

    public boolean isPlayerJoin() {
        return playerJoin;
    }

    public ModMdoConnectionSetting setPlayerJoin(boolean playerJoin) {
        this.playerJoin = playerJoin;
        return this;
    }

    public ModMdoConnectionSetting setChat(boolean chat) {
        this.chat = chat;
        return this;
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        json.put("chat", chat);
        json.put("playerJoin", playerJoin);
        json.put("playerQuit", playerQuit);
        json.put("name", name);
        json.put("testing", testing);
        return json;
    }

    public ModMdoConnectionSetting() {

    }

    public ModMdoConnectionSetting(JSONObject json) {
        this.chat = json.getBoolean("chat");
        this.playerJoin = json.getBoolean("playerJoin");
        this.playerQuit = json.getBoolean("playerQuit");
        this.name = json.getString("name");
        this.testing = json.getBoolean("testing");
    }

    public boolean isTesting() {
        return testing;
    }

    public void setTesting(boolean testing) {
        this.testing = testing;
    }

    public static ModMdoConnectionSetting localSettings() {
        ModMdoConnectionSetting setting = new ModMdoConnectionSetting();
        setting.setName(configCached.get("server_name"));
        setting.setChat(configCached.getConfigBoolean("modmdo_connection_chatting_accept"));
        setting.setPlayerJoin(configCached.getConfigBoolean("modmdo_connection_player_join_accept"));
        setting.setPlayerQuit(configCached.getConfigBoolean("modmdo_connection_player_quit_accept"));
        setting.setTesting(SharedVariables.testing);
        return setting;
    }
}
