package com.github.zhuaidadaya.modmdo.storage;

import com.github.zhuaidadaya.modmdo.cavas.CavaUtil;
import com.github.zhuaidadaya.modmdo.extra.loader.ModMdoExtraLoader;
import com.github.zhuaidadaya.modmdo.utils.command.SimpleCommandOperation;
import com.github.zhuaidadaya.modmdo.format.console.ConsoleTextFormat;
import com.github.zhuaidadaya.modmdo.format.minecraft.MinecraftTextFormat;
import com.github.zhuaidadaya.modmdo.jump.server.ServerUtil;
import com.github.zhuaidadaya.modmdo.lang.Language;
import com.github.zhuaidadaya.modmdo.login.server.ServerLogin;
import com.github.zhuaidadaya.modmdo.login.token.ClientEncryptionToken;
import com.github.zhuaidadaya.modmdo.login.token.EncryptionTokenUtil;
import com.github.zhuaidadaya.modmdo.login.token.ServerEncryptionToken;
import com.github.zhuaidadaya.modmdo.login.token.TokenContentType;
import com.github.zhuaidadaya.modmdo.mixins.MinecraftServerSession;
import com.github.zhuaidadaya.modmdo.ranking.Rank;
import com.github.zhuaidadaya.modmdo.type.ModMdoType;
import com.github.zhuaidadaya.modmdo.usr.User;
import com.github.zhuaidadaya.modmdo.usr.UserUtil;
import com.github.zhuaidadaya.modmdo.utils.config.DiskObjectConfigUtil;
import com.github.zhuaidadaya.modmdo.utils.config.ObjectConfigUtil;
import com.github.zhuaidadaya.modmdo.utils.dimension.DimensionUtil;
import com.github.zhuaidadaya.modmdo.utils.enchant.EnchantLevelController;
import com.mojang.brigadier.context.CommandContext;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.SocketAddress;
import java.text.NumberFormat;
import java.util.*;

public class Variables {
    public static final Logger LOGGER = LogManager.getLogger("ModMdo");
    public static final String VERSION_ID = "1.0.27";
    public static final int MODMDO_VERSION = 21;
    public static final UUID extraId = UUID.randomUUID();
    public static String rankingObject = "Nan";
    public static int rankingRandomSwitchInterval = 20 * 60 * 8;
    public static boolean rankingOnlineTimeScaleChanged = false;
    public static boolean rankingGameOnlineTimeScaleChanged = false;
    public static String rankingOnlineTimeScale = "minute";
    public static String rankingGameOnlineTimeScale = "minute";
    public static String entrust = "ModMdo";
    public static Language language = Language.ENGLISH;
    public static boolean rankingSwitchNoDump = true;
    public static boolean enableRanking = false;
    public static boolean enableHereCommand = true;
    public static boolean enableDeadMessage = true;
    public static boolean enableCava = true;
    public static boolean enableSecureEnchant = true;
    public static boolean enableRejectReconnect = true;
    public static boolean enableEncryptionToken = false;
    public static boolean cancelEntitiesTick = false;
    public static boolean enableCheckTokenPerTick = false;
    public static boolean forceStopTokenCheck = false;
    public static boolean timeActive = true;
    public static boolean tokenChanged = false;
    public static boolean rejectNoFallCheat = true;
    public static int tokenGenerateSize = 1024;
    public static int tokenCheckTimeLimit = 3000;
    public static Identifier modMdoServerChannel = new Identifier("modmdo:server");
    public static Identifier loginChannel = new Identifier("modmdo:token");
    public static UserUtil rejectUsers;
    public static UserUtil loginUsers;
    public static UserUtil users;
    public static DiskObjectConfigUtil configCached;
    public static ObjectConfigUtil config;
    public static CavaUtil cavas;
    public static String motd = "";
    public static MinecraftServer server;
    public static MinecraftClient client;
    public static ModMdoType modMdoType = ModMdoType.NONE;
    public static EncryptionTokenUtil modMdoToken;
    public static TextFieldWidget editToken;
    public static TextFieldWidget editLoginType;
    public static TextFieldWidget tokenTip;
    public static final Object2IntRBTreeMap<String> modMdoVersionToIdMap = new Object2IntRBTreeMap<>();
    public static final Object2ObjectRBTreeMap<Integer, String> modMdoIdToVersionMap = new Object2ObjectRBTreeMap<>();
    public static int itemDespawnAge = 6000;

    public static EnchantLevelController enchantLevelController;
    public static boolean needSync = false;
    public static boolean clearEnchantIfLevelTooHigh = false;

    public static ServerLogin serverLogin = new ServerLogin();
    public static final LinkedHashMap<ServerPlayerEntity, Long> skipMap = new LinkedHashMap<>();

    public static final LinkedHashSet<SocketAddress> disconnectedSet = new LinkedHashSet<>();

    public static ObjectArrayList<Rank> rankingObjects = new ObjectArrayList<>();
    public static ObjectArrayList<Rank> rankingObjectsNoDump = new ObjectArrayList<>();
    public static Object2ObjectArrayMap<String, Rank> supportedRankingObjects = new Object2ObjectArrayMap<>();

    public static ServerUtil servers = new ServerUtil();
    public static boolean connectTo = false;
    public static String jump = "";
    public static String jumpToken = "";
    public static String jumpLoginType = "";

    public static ConsoleTextFormat consoleTextFormat;
    public static MinecraftTextFormat minecraftTextFormat;

    public static ModMdoExtraLoader extras;
    public static boolean loaded = false;

    public static final NumberFormat fractionDigits2 = NumberFormat.getNumberInstance();
    public static final NumberFormat fractionDigits1 = NumberFormat.getNumberInstance();
    public static final NumberFormat fractionDigits0 = NumberFormat.getNumberInstance();

    public static void allDefault() {
        fractionDigits0.setGroupingUsed(false);
        fractionDigits0.setMinimumFractionDigits(0);
        fractionDigits0.setMaximumFractionDigits(0);

        fractionDigits1.setGroupingUsed(false);
        fractionDigits1.setMinimumFractionDigits(1);
        fractionDigits1.setMaximumFractionDigits(1);

        fractionDigits2.setGroupingUsed(false);
        fractionDigits2.setMinimumFractionDigits(2);
        fractionDigits2.setMaximumFractionDigits(2);

        rankingObject = "Nan";
        rankingRandomSwitchInterval = 20 * 60 * 8;
        rankingOnlineTimeScaleChanged = false;
        rankingGameOnlineTimeScaleChanged = false;
        rankingOnlineTimeScale = "minute";
        rankingGameOnlineTimeScale = "minute";
        language = Language.ENGLISH;
        rankingSwitchNoDump = true;
        enableRanking = false;
        enableHereCommand = true;
        enableDeadMessage = true;
        enableCava = true;
        enableSecureEnchant = true;
        enableRejectReconnect = true;
        enableEncryptionToken = false;
        cancelEntitiesTick = false;
        enableCheckTokenPerTick = false;
        forceStopTokenCheck = false;
        timeActive = true;
        tokenChanged = false;
        tokenGenerateSize = 1024;
        tokenCheckTimeLimit = 3000;
        modMdoServerChannel = new Identifier("modmdo:server");
        loginChannel = new Identifier("modmdo:token");
        rejectUsers = new UserUtil();
        loginUsers = new UserUtil();
        users = new UserUtil();
        cavas = new CavaUtil();
        motd = "";
        itemDespawnAge = 6000;
        language = Language.ENGLISH;

        rankingObjects = new ObjectArrayList<>();
        rankingObjectsNoDump = new ObjectArrayList<>();

        servers = new ServerUtil();
        connectTo = false;
        jump = "";
        jumpToken = "";
        jumpLoginType = "";

        enchantLevelController.setNoVanillaDefaultMaxLevel((short) 5);
        initEnchantmentMaxLevel();
    }

    public static void initEnchantmentMaxLevel() {
        try {
            JSONObject json = config.getConfigJSONObject("enchantment_level_limit");
            enchantLevelController.set(json);
        } catch (Exception e) {

        }
    }

    public static void saveEnchantmentMaxLevel() {
        config.set("enchantment_level_limit", enchantLevelController.toJSONObject());
    }

    public static void showScoreboard(MinecraftServer server, String name, String display) {
        ServerScoreboard scoreboard = server.getScoreboard();

        if (scoreboard.containsObjective(name)) {
            config.set("ranking_object", rankingObject = display);

            ((Scoreboard) scoreboard).setObjectiveSlot(1, scoreboard.getObjective(name));
        } else {
            throw new IllegalStateException();
        }
    }

    public static void addScoreboard(MinecraftServer server, Text displayName, String id) {
        ServerScoreboard scoreboard = server.getScoreboard();
        if (scoreboard.containsObjective(id)) {
            scoreboard.removeObjective(scoreboard.getObjective(id));
        }
        scoreboard.addObjective(id, ScoreboardCriterion.DUMMY, displayName, ScoreboardCriterion.DUMMY.getDefaultRenderType());
    }

    public static boolean rankingIsStatObject(String ranking) {
        if (supportedRankingObjects.get(ranking) == null) {
            return false;
        }
        return supportedRankingObjects.get(ranking).isStat();
    }

    public static String getRandomRankingObject() {
        if (rankingObjects.size() > 0) {
            Random r = new Random();
            return rankingObjects.toArray()[Math.max(0, r.nextInt(rankingObjects.size()))].toString();
        } else {
            return "Nan";
        }
    }

    public static String getRandomRankingObjectNoDump() {
        if (rankingObjectsNoDump.size() == 0) {
            rankingObjectsNoDump.addAll(rankingObjects.stream().toList());
        }
        if (rankingObjectsNoDump.size() > 0) {
            Random r = new Random();
            Rank ranking = (Rank) rankingObjectsNoDump.toArray()[Math.max(0, r.nextInt(rankingObjectsNoDump.size()))];
            rankingObjectsNoDump.remove(ranking);
            return ranking.getName();
        } else {
            return "Nan";
        }
    }

    public static String getServerLevelPath(MinecraftServer server) {
        return (server.isDedicated() ? "" : "saves/") + getServerLevelNamePath(server);
    }

    public static String getServerLevelNamePath(MinecraftServer server) {
        return ((MinecraftServerSession) server).getSession().getDirectoryName() + "/";
    }

    public static void saveToken() {
        try {
            new File("token/").mkdirs();
            BufferedWriter bw = new BufferedWriter(new FileWriter("token/token.txt"));
            bw.write(modMdoToken.getServerToken().checkToken("default"));
            bw.close();
            bw = new BufferedWriter(new FileWriter("token/token_ops.txt"));
            bw.write(modMdoToken.getServerToken().checkToken("ops"));
            bw.close();
            bw = new BufferedWriter(new FileWriter("token/token.json"));
            JSONObject tokenJson = modMdoToken.toJSONObject();
            tokenJson.remove("client");
            bw.write(tokenJson.toString());
            bw.close();
        } catch (Exception e) {

        }
    }

    public static String getApply(CommandContext<ServerCommandSource> source) {
        return getApply(SimpleCommandOperation.getServer(source));
    }

    public static String getApply(MinecraftServer server) {
        return ((MinecraftServerSession) server).getSession().getDirectoryName() + "/";
    }

    public static String getApply() {
        return getApply(server);
    }

    public static void sendMessageToAllPlayer(Text message, boolean actionBar) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList())
            sendMessage(player, message, actionBar);
    }

    public static void sendMessage(ServerPlayerEntity player, Text message, boolean actionBar) {
        player.sendMessage(message, actionBar);
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
     * @return 返回查询结果(或默认值)
     * @author 草awa
     * @author 草二号机
     */
    public static String getModMdoTokenFormat(String address, TokenContentType contentType) {
        String tokenString;
        String loginType;
        try {
            switch (contentType) {
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
            switch (contentType) {
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

    public static boolean equalsModMdoVersion(ServerPlayerEntity player) {
        return getPlayerModMdoVersion(player) == MODMDO_VERSION;
    }

    public static int getPlayerModMdoVersion(ServerPlayerEntity player) {
        try {
            return Integer.parseInt(loginUsers.getUser(player).getClientToken().getVersion());
        } catch (Exception e) {
            return - 1;
        }
    }

    public static boolean commandApplyToPlayer(int versionRequire, ServerPlayerEntity player, SimpleCommandOperation command, CommandContext<ServerCommandSource> source) {
        return commandApplyToPlayer(versionRequire, player, source.getSource());
    }

    public static boolean commandApplyToPlayer(int versionRequire, ServerPlayerEntity player, ServerCommandSource source) {
        if (! SimpleCommandOperation.getServer(source).isDedicated() || player == null || getFeatureCanUse(versionRequire, player))
            return true;

        SimpleCommandOperation.sendError(source, SimpleCommandOperation.formatModMdoVersionRequire(versionRequire, player));
        return false;
    }

    public static boolean getFeatureCanUse(int versionRequire, ServerPlayerEntity player) {
        return versionRequire <= getPlayerModMdoVersion(player);
    }

    public static void initModMdoToken() {
        try {
            JSONObject token = new JSONObject(config.getConfigString("token_by_encryption"));

            modMdoToken = new EncryptionTokenUtil();

            try {
                JSONObject clientTokens = token.getJSONObject("client");

                for (Object o : clientTokens.keySet()) {
                    JSONObject clientToken = clientTokens.getJSONObject(o.toString());
                    modMdoToken.addClientToken(new ClientEncryptionToken(clientToken.getString("token"), o.toString(), clientToken.getString("login_type"), VERSION_ID));
                }
            } catch (Exception e) {

            }

            JSONObject serverToken = token.getJSONObject("server");

            modMdoToken.setServerToken(new ServerEncryptionToken(serverToken.getString("default"), serverToken.getString("ops")));
        } catch (Exception e) {

        }
    }

    public static void updateModMdoVariables() {
        config.set("default_language", language.toString());
        config.set("here_command", enableHereCommand);
        config.set("dead_message", enableDeadMessage);
        config.set("cava", enableCava);
        config.set("secure_enchant", enableSecureEnchant);
        config.set("encryption_token", enableEncryptionToken);
        config.set("reject_reconnect", enableRejectReconnect);
        config.set("check_token_per_tick", enableCheckTokenPerTick);
        if (modMdoToken != null)
            config.set("token_by_encryption", modMdoToken.toJSONObject());
        config.set("time_active", timeActive);
        config.set("checker_time_limit", tokenCheckTimeLimit);
        config.set("enchantment_clear_if_level_too_high", clearEnchantIfLevelTooHigh);
        config.set("reject_no_fall_chest", rejectNoFallCheat);
    }

    public static void updateServersJump() {
        config.set("servers_jump", servers.toJSONObject());
    }

    public static void updateCavas() {
        config.set("cavas", cavas.toJSONObject());
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

    public static void updateUserProfiles() {
        config.set("user_profiles", users.toJSONObject());
    }

    public static void addUserFollow(User user, String... follows) {
        JSONObject userInfo;
        try {
            userInfo = users.getJSONObject(user.getID());
        } catch (Exception e) {
            userInfo = new JSONObject().put("uuid", user.getID()).put("name", user.getName());
        }
        user = new User(userInfo);
        user.addFollows(follows);
        users.put(user);

        updateUserProfiles();
    }

    public static void removeUserFollow(User user, String... follows) {
        JSONObject userInfo;
        try {
            userInfo = users.getJSONObject(user.getID());
        } catch (Exception e) {
            userInfo = new JSONObject().put("uuid", user.getID()).put("name", user.getName());
        }
        user = new User(userInfo);

        for (String s : follows) {
            user.removeFollow(s);
        }

        users.put(user);

        updateUserProfiles();
    }

    public static void clearUserFollow(User user) {
        JSONObject userInfo;
        try {
            userInfo = users.getJSONObject(user.getID());
        } catch (Exception e) {
            userInfo = new JSONObject().put("uuid", user.getID()).put("name", user.getName());
        }
        user = new User(userInfo);
        user.clearFollows();
        users.put(user);

        updateUserProfiles();
    }

    public static void sendFollowingMessage(PlayerManager players, Text message, String... follows) {
        try {
            for (ServerPlayerEntity player : players.getPlayerList()) {
                User staticUser = users.getUser(player.getUuid());
                HashSet<String> permissions = new HashSet<>();

                boolean unableToSend = false;

                for (String s : follows) {
                    permissions.add(config.getConfigString(s).toLowerCase(Locale.ROOT));
                }

                if (permissions.contains("unable"))
                    unableToSend = true;

                if (! unableToSend) {
                    boolean needOps = permissions.contains("ops");

                    if (staticUser.isFollow(follows)) {
                        if (needOps) {
                            if (player.hasPermissionLevel(4)) {
                                sendMessage(player, message, false);
                            }
                        } else {
                            sendMessage(player, message, false);
                        }
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    public static Language getLanguage() {
        return language;
    }

    public static Language getLanguage(Language lang) {
        return lang == null ? language : lang;
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
}