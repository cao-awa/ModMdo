package com.github.cao.awa.modmdo.storage;

import com.github.cao.awa.hyacinth.logging.*;
import com.github.cao.awa.modmdo.certificate.*;
import com.github.cao.awa.modmdo.commands.*;
import com.github.cao.awa.modmdo.develop.clazz.*;
import com.github.cao.awa.modmdo.enchant.*;
import com.github.cao.awa.modmdo.event.*;
import com.github.cao.awa.modmdo.event.trigger.*;
import com.github.cao.awa.modmdo.event.variable.*;
import com.github.cao.awa.modmdo.extra.loader.*;
import com.github.cao.awa.modmdo.format.console.*;
import com.github.cao.awa.modmdo.format.minecraft.*;
import com.github.cao.awa.modmdo.lang.Language;
import com.github.cao.awa.modmdo.mixins.server.*;
import com.github.cao.awa.modmdo.network.forwarder.process.*;
import com.github.cao.awa.modmdo.ranking.*;
import com.github.cao.awa.modmdo.security.key.*;
import com.github.cao.awa.modmdo.server.login.*;
import com.github.cao.awa.modmdo.type.*;
import com.github.cao.awa.modmdo.usr.*;
import com.github.cao.awa.modmdo.utils.command.*;
import com.github.cao.awa.modmdo.utils.entity.*;
import com.github.zhuaidadaya.rikaishinikui.handler.conductor.thread.*;
import com.github.zhuaidadaya.rikaishinikui.handler.config.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.runnable.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.runnable.delay.*;
import com.mojang.brigadier.context.*;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.scoreboard.*;
import net.minecraft.server.*;
import net.minecraft.server.command.*;
import net.minecraft.server.network.*;
import net.minecraft.server.world.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import org.apache.logging.log4j.*;
import org.json.*;

import java.text.*;
import java.util.*;
import java.util.concurrent.locks.*;

public class SharedVariables {
    public static final Logger LOGGER = LogManager.getLogger("ModMdo");
    public static final byte[] NONCE = "MODMDO:SERVER_NONCE_!+[RD]".getBytes();
    public static final String VERSION_ID = "1.0.40";
    public static final String SUFFIX = "-Debug";
    public static final String MODMDO_VERSION_NAME = VERSION_ID + SUFFIX;
    public static final String RELEASE_TIME = "2022.7.8";
    public static final int MODMDO_VERSION = 31;
    public static final UUID EXTRA_ID = UUID.fromString("1a6dbe1a-fea8-499f-82d1-cececcf78b7c");
    public static final Object2IntOpenHashMap<String> modMdoVersionToIdMap = new Object2IntOpenHashMap<>();
    public static final Int2ObjectOpenHashMap<String> modMdoIdToVersionMap = new Int2ObjectOpenHashMap<>();
    public static final NumberFormat fractionDigits2 = NumberFormat.getNumberInstance();
    public static final NumberFormat fractionDigits1 = NumberFormat.getNumberInstance();
    public static final NumberFormat fractionDigits0 = NumberFormat.getNumberInstance();
    public static final Identifier CHECKING_CHANNEL = new Identifier("modmdo:check");
    public static final Identifier LOGIN_CHANNEL = new Identifier("modmdo:login");
    public static final Identifier SERVER_CHANNEL = new Identifier("modmdo:server");
    public static final Identifier CLIENT_CHANNEL = new Identifier("modmdo:client");
    public static final Identifier DATA_CHANNEL = new Identifier("modmdo:data");
    public static final Identifier TOKEN_CHANNEL = new Identifier("modmdo:token");
    public static final Object2ObjectOpenHashMap<String, ModMdoPersistent<?>> variables = new Object2ObjectOpenHashMap<>();
    public static final GlobalTracker TRACKER = new GlobalTracker();
    public static final ObjectArrayList<ServerPlayerEntity> force = new ObjectArrayList<>();
    public static final SecureKeys SECURE_KEYS = new SecureKeys();
    public static final Object2ObjectArrayMap<ServerChunkManager, TaskOrder<ServerChunkManager>> chunkTasks = new Object2ObjectArrayMap<>();
    public static final MapCountDownConductor<ServerWorld, TaskOrder<ServerWorld>> blockEntitiesTasks = new MapCountDownConductor<>();
    public static String identifier;
    public static String entrust = "ModMdo";
    public static boolean enableRanking = false;
    public static boolean enableHereCommand = true;
    public static boolean enableSecureEnchant = true;
    public static boolean enableRejectReconnect = true;
    public static boolean cancelEntitiesTick = false;
    public static boolean timeActive = true;
    public static boolean rejectNoFallCheat = true;
    public static boolean modmdoWhitelist = false;
    public static Object2ObjectOpenHashMap<String, Long> loginTimedOut = new Object2ObjectOpenHashMap<>();
    public static Users rejectUsers;
    public static Users loginUsers;
    public static DiskObjectConfigUtil config;
    public static DiskObjectConfigUtil staticConfig;
    public static MinecraftServer server;
    public static ModMdoType modMdoType = ModMdoType.NONE;
    public static int itemDespawnAge = 6000;
    public static EnchantLevelController enchantLevelController;
    public static boolean clearEnchantIfLevelTooHigh = false;
    public static ServerLogin serverLogin = new ServerLogin();
    public static ObjectArrayList<ModMdoDataProcessor> modmdoConnections = new ObjectArrayList<>();
    public static TemporaryCertificate modmdoConnectionAccepting = new TemporaryCertificate("", - 1, - 1);
    public static Certificates<PermanentCertificate> modmdoConnectionWhitelist = new Certificates<>();
    public static Certificates<PermanentCertificate> whitelist = new Certificates<>();
    public static Certificates<TemporaryCertificate> temporaryStation = new Certificates<>();
    public static Certificates<TemporaryCertificate> temporaryInvite = new Certificates<>();
    public static Certificates<Certificate> banned = new Certificates<>();
    public static ConsoleTextFormat consoleTextFormat;
    public static MinecraftTextFormat minecraftTextFormat;
    public static ModMdoExtraLoader extras;
    public static boolean loaded = false;
    public static boolean debug = false;
    public static boolean testing = false;
    public static boolean testingShulker = false;
    public static boolean testingParallel = false;
    public static ModMdoCommandRegister commandRegister;
    public static ModMdoEventTracer event = new ModMdoEventTracer();
    public static ModMdoTriggerBuilder triggerBuilder = new ModMdoTriggerBuilder();
    public static ModMdoVariableBuilder variableBuilder = new ModMdoVariableBuilder();

    public static ClazzScanner CLAZZ_SCANNER = new ClazzScanner(ModMdoExtra.class);

    public static Object2ObjectOpenHashMap<String, Rank> ranking = new Object2ObjectOpenHashMap<>();

    public static final DelayTaskSequence delayTasks = new DelayTaskSequence();
    public static final InformTask informTask = new InformTask();

    public static final ReentrantLock publicLock = new ReentrantLock(true);

    //    public static ShareBackupLibrary backups = new ShareBackupLibrary();

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

        enableRanking = false;
        enableHereCommand = true;
        enableSecureEnchant = true;
        enableRejectReconnect = true;
        cancelEntitiesTick = false;
        timeActive = true;
        rejectUsers = new Users();
        loginUsers = new Users();
        itemDespawnAge = 6000;

        enchantLevelController.setNoVanillaDefaultMaxLevel((short) 5);

        temporaryInvite.clear();

        force.clear();

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
        temporaryStation.clear();
        whitelist.clear();
        modmdoConnectionWhitelist.clear();

        EntrustExecution.tryTemporary(() -> {
            JSONObject json = config.getConfigJSONObject("whitelist");

            json.keySet().parallelStream().forEach(s -> {
                whitelist.put(s, PermanentCertificate.build(json.getJSONObject(s)));
            });
        });

        EntrustExecution.tryTemporary(() -> {
            JSONObject json = config.getConfigJSONObject("connection-whitelist");

            json.keySet().parallelStream().forEach(s -> {
                modmdoConnectionWhitelist.put(s, PermanentCertificate.build(json.getJSONObject(s)));
            });
        });
    }

    public static void initBan() {
        banned.clear();

        EntrustExecution.tryTemporary(() -> {
            JSONObject json = config.getConfigJSONObject("banned");

            json.keySet().parallelStream().forEach(s -> {
                banned.put(s, Certificate.build(json.getJSONObject(s)));
            });
        });
    }

    public static void saveEnchantmentMaxLevel() {
        config.set("enchantment_level_limit", enchantLevelController.toJSONObject());
    }

    public static void addScoreboard(MinecraftServer server, Text displayName, String id) {
        ServerScoreboard scoreboard = server.getScoreboard();
        if (scoreboard.containsObjective(id)) {
            scoreboard.removeObjective(scoreboard.getObjective(id));
        }
        scoreboard.addObjective(id, ScoreboardCriterion.DUMMY, displayName, ScoreboardCriterion.DUMMY.getDefaultRenderType());
    }

    public static String getServerLevelPath(MinecraftServer server) {
        return (server.isDedicated() ? "" : "saves/") + getServerLevelNamePath(server);
    }

    public static String getServerLevelNamePath(MinecraftServer server) {
        return ((MinecraftServerInterface) server).getSession().getDirectoryName() + "/";
    }

    public static String getApply(CommandContext<ServerCommandSource> source) {
        return getApply(SimpleCommandOperation.getServer(source));
    }

    public static String getApply(MinecraftServer server) {
        return ((MinecraftServerInterface) server).getSession().getDirectoryName() + "/";
    }

    public static String getApply() {
        return getApply(server);
    }

    public static void sendMessageToAllPlayer(Text message, boolean actionBar) {
        sendMessageToAllPlayer(server, message, actionBar);
    }

    public static void sendMessageToAllPlayer(MinecraftServer server, Text message, boolean actionBar) {
        EntrustExecution.parallelTryFor(server.getPlayerManager().getPlayerList(), player -> sendMessage(player, message, actionBar));
    }

    public static void sendMessage(ServerPlayerEntity player, Text message, boolean actionBar) {
        player.sendMessage(message, actionBar);
    }

    public static boolean equalsModMdoVersion(ServerPlayerEntity player) {
        return getPlayerModMdoVersion(player) == MODMDO_VERSION;
    }

    public static int getPlayerModMdoVersion(ServerPlayerEntity player) {
        return EntrustParser.tryCreate(() -> loginUsers.getUser(player).getVersion(), - 1);
    }

    public static String getPlayerModMdoName(ServerPlayerEntity player) {
        return EntrustParser.trying(() -> loginUsers.getUser(player).getModmdoName());
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
        config.setIfNoExist("default_language", Language.EN_US);
        config.setIfNoExist("here_command", true);
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
        config.setIfNoExist("modmdo_connection_chatting_format", ModMdoDataProcessor.DEFAULT_CHAT_FORMAT);
        config.setIfNoExist("modmdo_connection_chatting_forward", true);
        config.setIfNoExist("modmdo_connection_chatting_accept", true);
        config.setIfNoExist("modmdo_connection_player_join_forward", true);
        config.setIfNoExist("modmdo_connection_player_quit_forward", true);
        config.setIfNoExist("modmdo_connection_player_join_accept", true);
        config.setIfNoExist("modmdo_connection_player_quit_accept", true);
    }

    public static void saveVariables(Temporary action) {
        action.apply();
        saveVariables();
    }

    public static void saveVariables() {
        config.set("here_command", enableHereCommand);
        config.set("secure_enchant", enableSecureEnchant);
        config.set("reject_reconnect", enableRejectReconnect);
        config.set("time_active", timeActive);
        config.set("enchantment_clear_if_level_too_high", clearEnchantIfLevelTooHigh);
        config.set("reject_no_fall_chest", rejectNoFallCheat);
        config.set("modmdo_whitelist", modmdoWhitelist);

        if (modMdoType == ModMdoType.SERVER) {
            EntrustExecution.tryTemporary(() -> {
                JSONObject json = new JSONObject();
                whitelist.keySet().parallelStream().forEach(s -> json.put(s, whitelist.get(s).toJSONObject()));
                config.set("whitelist", json);
            });

            EntrustExecution.tryTemporary(() -> {
                JSONObject json = new JSONObject();
                modmdoConnectionWhitelist.keySet().parallelStream().forEach(s -> json.put(s, modmdoConnectionWhitelist.get(s).toJSONObject()));
                config.set("connection-whitelist", json);
            });

            EntrustExecution.tryTemporary(() -> {
                JSONObject json = new JSONObject();
                banned.keySet().parallelStream().forEach(s -> json.put(s, banned.get(s).toJSONObject()));
                config.set("banned", json);
            });
        }
    }

    public static Language getLanguage() {
        return Language.ofs(config.getConfigString("default_language"));
    }

    public static void handleTemporaryWhitelist() {
        temporaryStation.values().parallelStream().filter(TemporaryCertificate::notValid).forEach(wl -> temporaryStation.remove(wl.getName()));
    }

    public static void handleTemporaryBan() {
        banned.keySet().parallelStream().forEach(name -> {
            Certificate ban = banned.get(name);
            if (ban == null) {
                banned.remove(name);
                return;
            }
            if (ban instanceof TemporaryCertificate temp) {
                if (temp.notValid()) {
                    banned.remove(name);
                }
            }
        });
    }

    public static boolean isActive() {
        return EntrustParser.trying(() -> extras.isActive(EXTRA_ID), () -> false);
    }

    public static boolean hasWhitelist(ServerPlayerEntity player) {
        try {
            if (temporaryInvite.containsName(EntityUtil.getName(player))) {
                if (temporaryInvite.get(EntityUtil.getName(player)).getMillions() == - 1) {
                    temporaryInvite.remove(EntityUtil.getName(player));
                    player.networkHandler.connection.send(new DisconnectS2CPacket(minecraftTextFormat.format(loginUsers.getUser(player), "modmdo.invite.canceled")));
                    player.networkHandler.connection.disconnect(minecraftTextFormat.format(loginUsers.getUser(player), "modmdo.invite.canceled"));
                    return true;
                }
                if (! temporaryInvite.get(EntityUtil.getName(player)).isValid()) {
                    temporaryInvite.remove(EntityUtil.getName(player));
                    player.networkHandler.connection.send(new DisconnectS2CPacket(minecraftTextFormat.format(loginUsers.getUser(player), "modmdo.invite.expired")));
                    player.networkHandler.connection.disconnect(minecraftTextFormat.format(loginUsers.getUser(player), "modmdo.invite.expired"));
                }
                return true;
            }
            if (config.getConfigBoolean("whitelist_only_id")) {
                return whitelist.getFromId(loginUsers.getUser(player).getIdentifier()) != null;
            }
            switch (whitelist.get(EntityUtil.getName(player)).getRecorde().type()) {
                case IDENTIFIER -> {
                    return whitelist.get(EntityUtil.getName(player)).getRecorde().getUniqueId().equals(loginUsers.getUser(player).getIdentifier());
                }
                case UUID -> {
                    if (Objects.requireNonNull(player.getServer()).isOnlineMode()) {
                        if (! player.getUuid().equals(whitelist.get(EntityUtil.getName(player)).getRecorde().getUuid())) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean handleBanned(ServerPlayerEntity player) {
        if (config.getConfigBoolean("modmdo_whitelist")) {
            if (hasBan(player)) {
                Certificate certificate = banned.get(EntityUtil.getName(player));
                if (certificate instanceof TemporaryCertificate temp) {
                    if (temp.isValid()) {
                        return true;
                    } else {
                        banned.remove(EntityUtil.getName(player));
                    }
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean hasBan(ServerPlayerEntity player) {
        try {
            switch (banned.get(EntityUtil.getName(player)).getRecorde().type()) {
                case IDENTIFIER -> {
                    if (banned.get(EntityUtil.getName(player)).getRecorde().getUniqueId().equals("")) {
                        return false;
                    }
                }
                case UUID -> {
                    if (! player.getUuid().equals(banned.get(EntityUtil.getName(player)).getRecorde().getUuid())) {
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
