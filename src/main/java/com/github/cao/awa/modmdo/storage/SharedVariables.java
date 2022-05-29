package com.github.cao.awa.modmdo.storage;

import com.github.cao.awa.modmdo.cavas.CavaUtil;
import com.github.cao.awa.modmdo.commands.ModMdoCommandRegister;
import com.github.cao.awa.modmdo.event.ModMdoEventTracer;
import com.github.cao.awa.modmdo.event.trigger.ModMdoTriggerBuilder;
import com.github.cao.awa.modmdo.event.variable.ModMdoPersistent;
import com.github.cao.awa.modmdo.event.variable.ModMdoVariableBuilder;
import com.github.cao.awa.modmdo.extra.loader.ModMdoExtra;
import com.github.cao.awa.modmdo.extra.loader.ModMdoExtraLoader;
import com.github.cao.awa.modmdo.format.console.ConsoleTextFormat;
import com.github.cao.awa.modmdo.format.minecraft.MinecraftTextFormat;
import com.github.cao.awa.modmdo.lang.Language;
import com.github.cao.awa.modmdo.mixins.MinecraftServerSession;
import com.github.cao.awa.modmdo.network.forwarder.process.ModMdoDataProcessor;
import com.github.cao.awa.modmdo.ranking.Rank;
import com.github.cao.awa.modmdo.server.login.ServerLogin;
import com.github.cao.awa.modmdo.subscribable.TickPerSecondAnalyzer;
import com.github.cao.awa.modmdo.type.ModMdoType;
import com.github.cao.awa.modmdo.utils.command.SimpleCommandOperation;
import com.github.cao.awa.modmdo.utils.enchant.EnchantLevelController;
import com.github.cao.awa.modmdo.utils.usr.User;
import com.github.cao.awa.modmdo.utils.usr.UserUtil;
import com.github.cao.awa.modmdo.whitelist.PermanentWhitelist;
import com.github.cao.awa.modmdo.whitelist.TemporaryWhitelist;
import com.github.cao.awa.modmdo.whitelist.WhiteLists;
import com.github.zhuaidadaya.rikaishinikui.handler.config.DiskObjectConfigUtil;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.EntrustExecution;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.EntrustParser;
import com.mojang.brigadier.context.CommandContext;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2IntRBTreeMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.SocketAddress;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class SharedVariables {
    public static final Logger LOGGER = LogManager.getLogger("ModMdo");
    public static final String VERSION_ID = "1.0.35";
    public static final String MODMDO_VERSION_NAME = VERSION_ID + "-ES";
    public static final String RELEASE_TIME = "2022.5.16";
    public static final int MODMDO_VERSION = 29;
    public static final UUID EXTRA_ID = UUID.fromString("1a6dbe1a-fea8-499f-82d1-cececcf78b7c");
    public static final Object2IntRBTreeMap<String> modMdoVersionToIdMap = new Object2IntRBTreeMap<>();
    public static final Object2ObjectRBTreeMap<Integer, String> modMdoIdToVersionMap = new Object2ObjectRBTreeMap<>();
    public static final NumberFormat fractionDigits2 = NumberFormat.getNumberInstance();
    public static final NumberFormat fractionDigits1 = NumberFormat.getNumberInstance();
    public static final NumberFormat fractionDigits0 = NumberFormat.getNumberInstance();
    public static final Identifier CHECKING = new Identifier("modmdo:check");
    public static final Identifier LOGIN = new Identifier("modmdo:login");
    public static final Identifier SERVER = new Identifier("modmdo:server");
    public static final Identifier CLIENT = new Identifier("modmdo:client");
    public static final Identifier DATA = new Identifier("modmdo:data");
    public static final Identifier TOKEN = new Identifier("modmdo:token");
    public static final Object2ObjectOpenHashMap<String, ModMdoPersistent<?>> variables = new Object2ObjectOpenHashMap<>();
    public static String identifier;
    public static String rankingObject = "Nan";
    public static int rankingRandomSwitchInterval = 20 * 60 * 8;
    public static boolean rankingOnlineTimeScaleChanged = false;
    public static String rankingOnlineTimeScale = "minute";
    public static String entrust = "ModMdo";
    public static boolean rankingSwitchNoDump = true;
    public static boolean enableRanking = false;
    public static boolean enableHereCommand = true;
    public static boolean enableDeadMessage = true;
    public static boolean enableCava = true;
    public static boolean enableSecureEnchant = true;
    public static boolean enableRejectReconnect = true;
    public static boolean cancelEntitiesTick = false;
    public static boolean timeActive = true;
    public static boolean rejectNoFallCheat = true;
    public static boolean modmdoWhitelist = false;
    public static boolean enableBlockRecorder = false;
    public static UserUtil rejectUsers;
    public static UserUtil loginUsers;
    public static UserUtil users;
    public static DiskObjectConfigUtil config;
    public static DiskObjectConfigUtil staticConfig;
    public static CavaUtil cavas;
    public static MinecraftServer server;
    public static ModMdoType modMdoType = ModMdoType.NONE;
    public static int itemDespawnAge = 6000;
    public static EnchantLevelController enchantLevelController;
    public static boolean clearEnchantIfLevelTooHigh = false;
    public static ServerLogin serverLogin = new ServerLogin();
    public static ObjectArrayList<Rank> rankingObjects = new ObjectArrayList<>();
    public static ObjectArrayList<Rank> rankingObjectsNoDump = new ObjectArrayList<>();
    public static Object2ObjectArrayMap<String, Rank> supportedRankingObjects = new Object2ObjectArrayMap<>();
    public static ObjectArrayList<String> modmdoConnectionNames = new ObjectArrayList<>();
    public static ObjectArrayList<ModMdoDataProcessor> modmdoConnections = new ObjectArrayList<>();
    public static TemporaryWhitelist modmdoConnectionAccepting = new TemporaryWhitelist("", - 1, - 1);
    public static WhiteLists<PermanentWhitelist> modmdoConnectionWhitelist = new WhiteLists<>();
    public static WhiteLists<PermanentWhitelist> whitelist = new WhiteLists<>();
    public static WhiteLists<TemporaryWhitelist> temporaryWhitelist = new WhiteLists<>();
    public static int whitelistHash = whitelist.hashCode();
    public static int temporaryWhitelistHash = temporaryWhitelist.hashCode();
    public static ConsoleTextFormat consoleTextFormat;
    public static MinecraftTextFormat minecraftTextFormat;
    public static ArrayList<ModMdoExtra<?>> extrasWaitingForRegister = new ArrayList<>();
    public static ModMdoExtraLoader extras;
    public static boolean loaded = false;
    public static boolean testing = false;
    public static ModMdoCommandRegister commandRegister;
    public static ModMdoEventTracer event;
    public static ModMdoTriggerBuilder triggerBuilder = new ModMdoTriggerBuilder();
    public static ModMdoVariableBuilder variableBuilder = new ModMdoVariableBuilder();
    public static TickPerSecondAnalyzer tps = new TickPerSecondAnalyzer();

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
        rankingOnlineTimeScale = "minute";
        rankingSwitchNoDump = true;
        enableRanking = false;
        enableHereCommand = true;
        enableDeadMessage = true;
        enableCava = true;
        enableSecureEnchant = true;
        enableRejectReconnect = true;
        cancelEntitiesTick = false;
        timeActive = true;
        rejectUsers = new UserUtil();
        loginUsers = new UserUtil();
        users = new UserUtil();
        cavas = new CavaUtil();
        itemDespawnAge = 6000;

        rankingObjects = new ObjectArrayList<>();
        rankingObjectsNoDump = new ObjectArrayList<>();

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

    public static void initWhiteList() {
        temporaryWhitelist = new WhiteLists<>();

        EntrustExecution.tryTemporary(() -> {
            JSONObject json = config.getConfigJSONObject("whitelist");

            for (String s : json.keySet()) {
                whitelist.put(s, PermanentWhitelist.build(json.getJSONObject(s)));
            }
        });

        EntrustExecution.tryTemporary(() -> {
            JSONObject json = config.getConfigJSONObject("connection-whitelist");

            for (String s : json.keySet()) {
                modmdoConnectionWhitelist.put(s, PermanentWhitelist.build(json.getJSONObject(s)));
            }
        });
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
        sendMessageToAllPlayer(server, message, actionBar);
    }

    public static void sendMessageToAllPlayer(MinecraftServer server, Text message, boolean actionBar) {
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

    public static boolean equalsModMdoVersion(ServerPlayerEntity player) {
        return getPlayerModMdoVersion(player) == MODMDO_VERSION;
    }

    public static int getPlayerModMdoVersion(ServerPlayerEntity player) {
        return EntrustParser.tryCreate(() -> loginUsers.getUser(player).getVersion(), - 1);
    }

    public static boolean commandApplyToPlayer(int versionRequire, ServerPlayerEntity player, CommandContext<ServerCommandSource> source) {
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

    public static void defaultConfig() {
        config.setIfNoExist("default_language", Language.ENGLISH);
        config.setIfNoExist("here_command", true);
        config.setIfNoExist("dead_message", true);
        config.setIfNoExist("cava", true);
        config.setIfNoExist("secure_enchant", true);
        config.setIfNoExist("modmdo_whitelist", false);
        config.setIfNoExist("reject_reconnect", true);
        config.setIfNoExist("time_active", true);
        config.setIfNoExist("checker_time_limit", 3000);
        config.setIfNoExist("enchantment_clear_if_level_too_high", false);
        config.setIfNoExist("reject_no_fall_chest", true);
        config.setIfNoExist("whitelist_only_id", false);
        config.setIfNoExist("compatible_online_mode", true);
        config.setIfNoExist("modmdo_connecting", true);
        config.setIfNoExist("modmdo_connecting_whitelist", new JSONObject());
        config.setIfNoExist("modmdo_connection_chatting_format", ModMdoDataProcessor.CONSOLE_CHAT_FORMAT);
        config.setIfNoExist("modmdo_connection_chatting_forward", true);
        config.setIfNoExist("modmdo_connection_chatting_accept", true);
        config.setIfNoExist("modmdo_connection_player_join_forward", true);
        config.setIfNoExist("modmdo_connection_player_quit_forward", true);
        config.setIfNoExist("modmdo_connection_player_join_accept", true);
        config.setIfNoExist("modmdo_connection_player_quit_accept", true);
    }

    public static void saveVariables() {
        config.set("here_command", enableHereCommand);
        config.set("dead_message", enableDeadMessage);
        config.set("cava", enableCava);
        config.set("secure_enchant", enableSecureEnchant);
        config.set("reject_reconnect", enableRejectReconnect);
        config.set("time_active", timeActive);
        config.set("enchantment_clear_if_level_too_high", clearEnchantIfLevelTooHigh);
        config.set("reject_no_fall_chest", rejectNoFallCheat);
        config.set("whitelist_only_id", false);
        config.set("modmdo_whitelist", modmdoWhitelist);

        if (modMdoType == ModMdoType.SERVER) {
            EntrustExecution.tryTemporary(() -> {
                JSONObject json = new JSONObject();
                for (String s : whitelist.keySet()) {
                    json.put(s, whitelist.get(s).toJSONObject());
                }
                config.set("whitelist", json);
            });

            EntrustExecution.tryTemporary(() -> {
                JSONObject json = new JSONObject();
                for (String s : modmdoConnectionWhitelist.keySet()) {
                    json.put(s, modmdoConnectionWhitelist.get(s).toJSONObject());
                }
                config.set("connection-whitelist", json);
            }, Throwable::printStackTrace);
        }
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

    public static Language getLanguage() {
        return Language.ofs(config.getConfigString("default_language"));
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

    public static void updateWhitelistNames(MinecraftServer server, boolean force) {
        if (! force) {
            if (whitelist.hashCode() != whitelistHash) {
                return;
            }
        }
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            player.networkHandler.connection.send(new CustomPayloadS2CPacket(SERVER, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(DATA).writeString("whitelist_names").writeString(getWhiteListNamesJSONObject().toString())));
        }
        whitelistHash = whitelist.hashCode();
    }

    public static JSONObject getWhiteListNamesJSONObject() {
        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();
        for (String s : whitelist.keySet()) {
            array.put(s);
        }
        json.put("names", array);
        return json;
    }

    public static void updateTemporaryWhitelistNames(MinecraftServer server, boolean force) {
        if (! force) {
            if (temporaryWhitelist.hashCode() == temporaryWhitelistHash) {
                return;
            }
        }
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            player.networkHandler.connection.send(new CustomPayloadS2CPacket(SERVER, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(DATA).writeString("temporary_whitelist_names").writeString(getTemporaryWhitelistHashNamesJSONObject().toString())));
        }
        temporaryWhitelistHash = temporaryWhitelist.hashCode();
    }

    public static JSONObject getTemporaryWhitelistHashNamesJSONObject() {
        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();
        for (String s : temporaryWhitelist.keySet()) {
            array.put(s);
        }
        json.put("names", array);
        return json;
    }

    public static void updateModMdoConnectionsNames(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            player.networkHandler.connection.send(new CustomPayloadS2CPacket(SERVER, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(DATA).writeString("connections").writeString(getModMdoConnectionsNamesJSONObject().toString())));
        }
    }

    public static JSONObject getModMdoConnectionsNamesJSONObject() {
        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();
        for (ModMdoDataProcessor processor : modmdoConnections) {
            array.put(processor.getModMdoConnection().getName());
        }
        json.put("names", array);
        return json;
    }

    public static void flushTemporaryWhitelist() {
        for (TemporaryWhitelist wl : temporaryWhitelist.values()) {
            if (! wl.isValid()) {
                temporaryWhitelist.remove(wl.name());
            }
        }
    }

    public static void registerExtra(ModMdoExtra<?> extra) {
        if (extras == null) {
            extrasWaitingForRegister.add(extra);
        } else {
            extras.register(extra.getId(), extra);
        }
    }

    public static boolean isActive() {
        return EntrustParser.trying(() -> extras.isActive(EXTRA_ID), () -> false);
    }
}
