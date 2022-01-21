package com.github.zhuaidadaya.modMdo.storage;

import com.github.zhuaidadaya.modMdo.bak.BackupUtil;
import com.github.zhuaidadaya.modMdo.cavas.CavaUtil;
import com.github.zhuaidadaya.modMdo.commands.DimensionTips;
import com.github.zhuaidadaya.modMdo.commands.SimpleCommandOperation;
import com.github.zhuaidadaya.modMdo.lang.Language;
import com.github.zhuaidadaya.modMdo.login.server.ServerLogin;
import com.github.zhuaidadaya.modMdo.login.token.ClientEncryptionToken;
import com.github.zhuaidadaya.modMdo.login.token.EncryptionTokenUtil;
import com.github.zhuaidadaya.modMdo.login.token.ServerEncryptionToken;
import com.github.zhuaidadaya.modMdo.login.token.TokenContentType;
import com.github.zhuaidadaya.modMdo.mixins.MinecraftServerSession;
import com.github.zhuaidadaya.modMdo.projects.ProjectUtil;
import com.github.zhuaidadaya.modMdo.type.ModMdoType;
import com.github.zhuaidadaya.modMdo.usr.User;
import com.github.zhuaidadaya.modMdo.usr.UserUtil;
import com.github.zhuaidadaya.utils.config.ObjectConfigUtil;
import com.mojang.brigadier.context.CommandContext;
import it.unimi.dsi.fastutil.objects.Object2IntRBTreeMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.*;
import java.net.SocketAddress;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.UUID;

public class Variables {
    public static final Logger LOGGER = LogManager.getLogger("ModMdo");
    public static String VERSION_ID = "1.0.13";
    public static int MODMDO_VERSION = 7;
    public static String entrust = "ModMdo";
    public static Language language = Language.ENGLISH;
    public static boolean enableHereCommand = true;
    public static boolean enableDeadMessage = true;
    public static boolean enableCava = true;
    public static boolean enableSecureEnchant = true;
    public static boolean enableRejectReconnect = true;
    public static boolean enableEncryptionToken = false;
    public static boolean enableTickAnalyzer = false;
    public static boolean enabledCancelEntitiesTIck = false;
    public static boolean enabledCancelTIck = false;
    public static long cancelTickStart = - 1;
    public static int tokenGenerateSize = 128;
    public static Identifier modMdoServerChannel = new Identifier("modmdo:server");
    public static Identifier tokenChannel = new Identifier("modmdo:token");
    public static UserUtil rejectUsers;
    public static UserUtil loginUsers;
    public static UserUtil users;
    public static ObjectConfigUtil config;
    public static ProjectUtil projects;
    public static CavaUtil cavas;
    public static String motd = "";
    public static MinecraftServer server;
    public static BackupUtil bak;
    public static ModMdoType modMdoType = ModMdoType.NONE;
    public static EncryptionTokenUtil modMdoToken = null;
    public static TextFieldWidget editToken;
    public static TextFieldWidget editLoginType;
    public static TextFieldWidget tokenTip;
    public static DimensionTips dimensionTips = new DimensionTips();
    public static Object2IntRBTreeMap<String> modMdoVersionToIdMap = new Object2IntRBTreeMap<>();
    public static Object2ObjectRBTreeMap<Integer, String> modMdoIdToVersionMap = new Object2ObjectRBTreeMap<>();
    public static Object2IntRBTreeMap<String> modMdoCommandVersionMap = new Object2IntRBTreeMap<>();

    public static String MODMDO_COMMAND_ROOT = "/";
    public static String MODMDO_COMMAND_CONF = "modmdo/";
    public static String MODMDO_COMMAND_TICK = "modmdo/tick/";
    public static String MODMDO_COMMAND_HERE = "here/";
    public static String MODMDO_COMMAND_CAVA = "cava/";
    public static String MODMDO_COMMAND_BAK = "bak/";
    public static String MODMDO_COMMAND_USR = "user/";
    public static String MODMDO_COMMAND_ANALYZER = "analyzer/";
    public static String MODMDO_COMMAND_TOKEN = "token/";

    public static int itemDespawnAge = 6000;

    public static ServerLogin serverLogin = new ServerLogin();
    public static LinkedHashMap<ServerPlayerEntity, Long> skipMap = new LinkedHashMap<>();
    public static LinkedHashSet<ServerPlayerEntity> playersChunkSendCache = new LinkedHashSet<>();

    public static int analyzedTick = 0;
    public static boolean shortAnalyze = true;
    public static String tickAnalyzerFile = "logs/tick_analyzer/";
    public static String tickStartTime;
    public static LinkedHashMap<String, Long> tickMap = new LinkedHashMap<>();
    public static LinkedHashMap<String, LinkedHashMap<String, Integer>> tickEntitiesMap = new LinkedHashMap<>();

    public static void saveToken() {
        try {
            new File("token/").mkdirs();
            BufferedWriter bw = new BufferedWriter(new FileWriter("token/token.txt"));
            bw.write(modMdoToken.getServerToken().checkToken("default"));
            bw.close();
            bw = new BufferedWriter(new FileWriter("token/token_ops.txt"));
            bw.write(modMdoToken.getServerToken().checkToken("ops"));
            bw.close();
        } catch (IOException e) {

        }
    }

    public static void sendMessageToPlayer(ServerPlayerEntity player, Text message, boolean actionBar) {
        player.sendMessage(message, actionBar);
    }

    public static String getApply(MinecraftServer server) {
        return ((MinecraftServerSession) server).getSession().getDirectoryName() + "/";
    }

    public static String getApply(SimpleCommandOperation command, CommandContext<ServerCommandSource> source) {
        return getApply(command.getServer(source));
    }

    public static String getApply() {
        return getApply(server);
    }

    public static void sendMessageToAllPlayer(Text message, boolean actionBar) {
        for(ServerPlayerEntity player : server.getPlayerManager().getPlayerList())
            sendMessageToPlayer(player, message, actionBar);
    }

    public static String formatAddress(SocketAddress socketAddress) {
        String address = socketAddress.toString();

        try {
            return address.substring(0, address.indexOf("/")) + ":" + address.substring(address.lastIndexOf(":") + 1);
        } catch (Exception e) {
            return address;
        }
    }

    /**
     * @param address
     *         通过指定IP查询
     * @param contentType
     *         查询类型, 可选查询token和登入方式
     *
     * @return 返回查询结果(或默认值)
     *
     * @author 草awa
     * @author 草二号机
     */
    public static String getModMdoTokenFormat(String address, TokenContentType contentType) {
        String tokenString;
        String loginType;
        try {
            switch(contentType) {
                case TOKEN_BY_ENCRYPTION -> {
                    tokenString = modMdoToken.getClientToken(address).getToken();
                    return tokenString;
                }
                case LOGIN_TYPE -> {
                    loginType = modMdoToken.getClientToken(address).getType();
                    return loginType;
                }
            }
        } catch (Exception e) {
            switch(contentType) {
                case TOKEN_BY_ENCRYPTION -> {
                    return "";
                }
                case LOGIN_TYPE -> {
                    return "default";
                }
            }
        }

        return "";
    }

    public static int getPlayerModMdoVersion(ServerPlayerEntity player) {
        try {
            return Integer.parseInt(loginUsers.getUser(player).getClientToken().getVersion());
        }catch (Exception e) {
            return -1;
        }
    }

    public static boolean equalsModMdoVersion(ServerPlayerEntity player) {
        return getPlayerModMdoVersion(player) == MODMDO_VERSION;
    }

    public static boolean commandApplyToPlayer(String commandBelong, ServerPlayerEntity player, SimpleCommandOperation command, CommandContext<ServerCommandSource> source) {
        if(getCommandCanUse(commandBelong, player))
            return true;

        command.sendError(source, command.formatModMdoVersionRequire(commandBelong));
        return false;
    }

    public static boolean commandApplyToPlayer(String commandBelong, ServerPlayerEntity player, SimpleCommandOperation command, ServerCommandSource source) {
        if(getCommandCanUse(commandBelong, player))
            return true;

        source.sendError(command.formatModMdoVersionRequire(commandBelong));
        return false;
    }

    public static boolean getCommandCanUse(String commandBelong, ServerPlayerEntity player) {
        return modMdoCommandVersionMap.getInt(commandBelong) <= getPlayerModMdoVersion(player);
    }

    public static String getCommandRequestVersion(String commandBelong) {
        return modMdoIdToVersionMap.get(modMdoCommandVersionMap.getInt( commandBelong));
    }

    public static void initModMdoToken() {
        try {
            JSONObject token = new JSONObject(config.getConfigString("token_by_encryption"));

            modMdoToken = new EncryptionTokenUtil();

            try {
                JSONObject clientTokens = token.getJSONObject("client");
                for(Object o : clientTokens.keySet()) {
                    JSONObject clientToken = clientTokens.getJSONObject(o.toString());
                    modMdoToken.addClientToken(new ClientEncryptionToken(clientToken.getString("token"), o.toString(), clientToken.getString("login_type"), ""));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            JSONObject serverToken = token.getJSONObject("server");

            modMdoToken.setServerToken(new ServerEncryptionToken(serverToken.getString("default"), serverToken.getString("ops")));
        } catch (Exception e) {

        }
    }

    public static void updateModMdoVariables() {
        config.set("default_language", language.toString());
        config.set("here_command", hereCommandStatus());
        config.set("dead_message", deadMessageStatus());
        config.set("cava", cavaStatus());
        config.set("secure_enchant", secureEnchantStatus());
        config.set("encryption_token", encryptionTokenStatus());
        config.set("reject_reconnect", rejectReconnectStatus());
        if(modMdoToken != null)
            config.set("token_by_encryption", modMdoToken.toJSONObject());
    }

    public static void updateUserProfiles() {
        config.set("user_profiles", users.toJSONObject());
    }

    public static void updateProjects() {
        config.set("projects", projects.toJSONObject());
    }

    public static void updateCavas() {
        config.set("cavas", cavas.toJSONObject());
    }

    public static void updateBackups() {
        try {
            config.set("backups", bak.toJSONObject());
        } catch (SyncFailedException e) {

        }
    }

    public static void setUserProfile(User user, String changeKey, String changeValue) {
        JSONObject userInfo;
        try {
            userInfo = users.getJSONObject(user.getID());
        } catch (Exception e) {
            userInfo = new JSONObject().put("uuid", user.getID()).put("name", user.getName());
        }
        userInfo.put(changeKey, changeValue);
        users.put(user.getID(), userInfo);

        updateUserProfiles();
    }

    public static Language getLanguage() {
        return language;
    }

    public static Language getLanguage(Language lang) {
        return lang == null ? language : lang;
    }

    public static Language getUserLanguage(UUID userUUID) {
        try {
            return getLanguage(Language.getLanguageForName(users.getUserConfig(userUUID.toString(), "language").toString()));
        } catch (Exception e) {
            return language;
        }
    }

    public static Language getUserLanguage(ServerPlayerEntity player) {
        return getUserLanguage(player.getUuid());
    }

    public static boolean isUserHereReceive(UUID userUUID) {
        try {
            return users.getUserConfig(userUUID.toString(), "receiveHereMessage").toString().equals("receive");
        } catch (Exception e) {
            return enableHereCommand;
        }
    }

    public static String getUserHereReceive(UUID userUUID) {
        try {
            return users.getUserConfig(userUUID.toString(), "receiveHereMessage").toString();
        } catch (Exception e) {
            return enableHereCommand ? "receive" : "rejected";
        }
    }

    public static boolean isUserDeadMessageReceive(UUID userUUID) {
        try {
            return users.getUserConfig(userUUID.toString(), "receiveDeadMessage").toString().equals("receive");
        } catch (Exception e) {
            return enableDeadMessage;
        }
    }

    public static String getUserDeadMessageReceive(UUID userUUID) {
        try {
            return users.getUserConfig(userUUID.toString(), "receiveDeadMessage").toString();
        } catch (Exception e) {
            return enableDeadMessage ? "receive" : "rejected";
        }
    }

    public static String hereCommandStatus() {
        return enableHereCommand ? "enable" : "disable";
    }

    public static String deadMessageStatus() {
        return enableDeadMessage ? "enable" : "disable";
    }

    public static String cavaStatus() {
        return enableCava ? "enable" : "disable";
    }

    public static String secureEnchantStatus() {
        return enableSecureEnchant ? "enable" : "disable";
    }

    public static String encryptionTokenStatus() {
        return enableEncryptionToken ? "enable" : "disable";
    }

    public static String rejectReconnectStatus() {
        return enableRejectReconnect ? "enable" : "disable";
    }
}
