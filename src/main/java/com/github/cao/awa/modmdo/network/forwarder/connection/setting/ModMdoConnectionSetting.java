package com.github.cao.awa.modmdo.network.forwarder.connection.setting;

import com.github.cao.awa.modmdo.storage.*;
import org.json.*;

public class ModMdoConnectionSetting {
    private boolean chat = true;
    private boolean playerJoin = true;
    private boolean playerQuit = true;

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
        return json;
    }

    public ModMdoConnectionSetting() {

    }

    public ModMdoConnectionSetting(JSONObject json) {
        this.chat = json.getBoolean("chat");
        this.playerJoin = json.getBoolean("playerJoin");
        this.playerQuit = json.getBoolean("playerQuit");
        this.name = json.getString("name");
    }

    public static ModMdoConnectionSetting localSettings() {
        ModMdoConnectionSetting setting = new ModMdoConnectionSetting();
        setting.setName(SharedVariables.config.get("server_name"));
        setting.setChat(SharedVariables.config.getConfigBoolean("modmdo_connection_chatting_accept"));
        setting.setPlayerJoin(SharedVariables.config.getConfigBoolean("modmdo_connection_player_join_accept"));
        setting.setPlayerQuit(SharedVariables.config.getConfigBoolean("modmdo_connection_player_quit_accept"));
        return setting;
    }
}
