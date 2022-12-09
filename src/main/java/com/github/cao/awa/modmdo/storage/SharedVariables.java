package com.github.cao.awa.modmdo.storage;

import com.github.cao.awa.modmdo.attack.ddos.recorder.*;
import com.github.cao.awa.modmdo.certificate.*;
import com.github.cao.awa.modmdo.commands.*;
import com.github.cao.awa.modmdo.config.*;
import com.github.cao.awa.modmdo.develop.clazz.*;
import com.github.cao.awa.modmdo.event.*;
import com.github.cao.awa.modmdo.event.trigger.*;
import com.github.cao.awa.modmdo.event.variable.*;
import com.github.cao.awa.modmdo.extra.loader.*;
import com.github.cao.awa.modmdo.format.console.*;
import com.github.cao.awa.modmdo.format.minecraft.*;
import com.github.cao.awa.modmdo.lang.Language;
import com.github.cao.awa.modmdo.mixins.server.*;
import com.github.cao.awa.modmdo.security.certificate.*;
import com.github.cao.awa.modmdo.security.key.*;
import com.github.cao.awa.modmdo.server.login.*;
import com.github.cao.awa.modmdo.type.*;
import com.github.cao.awa.modmdo.usr.*;
import com.github.cao.awa.modmdo.utils.command.*;
import com.github.cao.awa.modmdo.utils.entity.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.receptacle.*;
import com.mojang.brigadier.context.*;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.network.*;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.scoreboard.*;
import net.minecraft.server.*;
import net.minecraft.server.command.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import org.apache.logging.log4j.*;
import org.json.*;

import java.text.*;
import java.util.*;

public class SharedVariables {
    public static final Logger LOGGER = LogManager.getLogger("ModMdo");
    public static final byte[] MODMDO_NONCE = "MODMDO:SERVER_NONCE_!+[RD]".getBytes();
    public static final byte[] MODMDO_NONCE_HEAD = "MODMDO:SERVER_NONCE_!+".getBytes();
    public static final String TEST_MODMDO_VERSION = "10410004";
    public static final String VERSION_ID = "1.0.41";
    public static final String SUFFIX = "-ES";
    public static final String MODMDO_VERSION_NAME = VERSION_ID + SUFFIX;
    public static final String RELEASE_TIME = "UTC+8 2022.8.5";
    public static final String ENTRUST = "ModMdo";
    public static final int MODMDO_VERSION = 31;
    public static final UUID EXTRA_ID = UUID.fromString("1a6dbe1a-fea8-499f-82d1-cececcf78b7c");
    public static final Object2IntOpenHashMap<String> MOD_MDO_VERSION_TO_ID_MAP = new Object2IntOpenHashMap<>();
    public static final Int2ObjectOpenHashMap<String> MOD_MDO_ID_TO_VERSION_MAP = new Int2ObjectOpenHashMap<>();
    public static final NumberFormat FRACTION_DIGITS_2 = NumberFormat.getNumberInstance();
    public static final NumberFormat FRACTION_DIGITS_1 = NumberFormat.getNumberInstance();
    public static final NumberFormat FRACTION_DIGITS_0 = NumberFormat.getNumberInstance();
    public static final Identifier CHECKING_CHANNEL = new Identifier("modmdo:check");
    public static final Identifier LOGIN_CHANNEL = new Identifier("modmdo:login");
    public static final Identifier SERVER_CHANNEL = new Identifier("modmdo:server");
    public static final Identifier CLIENT_CHANNEL = new Identifier("modmdo:client");
    public static final Identifier TOKEN_CHANNEL = new Identifier("modmdo:token");
    public static final Object2ObjectOpenHashMap<String, ModMdoPersistent<?>> VARIABLES = new Object2ObjectOpenHashMap<>();
    public static final ObjectArrayList<ServerPlayerEntity> FORCE = new ObjectArrayList<>();
    public static final SecureKeys SECURE_KEYS = new SecureKeys();
    public static final ClazzScanner EXTRA_AUTO = new ClazzScanner(ModMdoExtra.class);
    public static final List<ClientConnection> CONNECTIONS = new ObjectArrayList<>();
    public static final List<DdosAttackRecorder> ddosAttackRecorders = new ObjectArrayList<>();
    public static String identifier;
    public static boolean enableRanking = false;
    public static boolean enableHereCommand = true;
    public static boolean enableSecureEnchant = true;
    public static boolean enableRejectReconnect = true;
    public static boolean timeActive = true;
    public static boolean modmdoWhitelist = false;
    public static Object2ObjectOpenHashMap<String, Long> loginTimedOut = new Object2ObjectOpenHashMap<>();
    public static Users rejectUsers;
    public static Users loginUsers;
    public static DiskConfigUtil config;
    public static DiskConfigUtil staticConfig;
    public static MinecraftServer server;
    public static ModMdoType modMdoType = ModMdoType.NONE;
    public static int itemDespawnAge = 6000;
    public static ServerLogin serverLogin = new ServerLogin();
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
    public static ModMdoCommandRegister commandRegister;
    public static ModMdoEventTracer event = new ModMdoEventTracer();
    public static ModMdoTriggerBuilder triggerBuilder = new ModMdoTriggerBuilder();
    public static ModMdoVariableBuilder variableBuilder = new ModMdoVariableBuilder();
    public static JSONObject notes = new JSONObject();
    public static Receptacle<Boolean> serverUnderDdosAttack = Receptacle.of(false);
    public static DdosAttackRecorder ddosRecording = null;

    public static long currentLogin = 0;

    public static void allDefault() {
        FRACTION_DIGITS_0.setGroupingUsed(false);
        FRACTION_DIGITS_0.setMinimumFractionDigits(0);
        FRACTION_DIGITS_0.setMaximumFractionDigits(0);

        FRACTION_DIGITS_1.setGroupingUsed(false);
        FRACTION_DIGITS_1.setMinimumFractionDigits(1);
        FRACTION_DIGITS_1.setMaximumFractionDigits(1);

        FRACTION_DIGITS_2.setGroupingUsed(false);
        FRACTION_DIGITS_2.setMinimumFractionDigits(2);
        FRACTION_DIGITS_2.setMaximumFractionDigits(2);

        enableRanking = false;
        enableHereCommand = true;
        enableSecureEnchant = true;
        enableRejectReconnect = true;
        timeActive = true;
        rejectUsers = new Users();
        loginUsers = new Users();
        itemDespawnAge = 6000;

        temporaryInvite.clear();

        FORCE.clear();
    }

    public static void initWhiteList() {
        temporaryStation.clear();
        whitelist.clear();

        EntrustEnvironment.trys(() -> {
            JSONObject json = config.getJSONObject("whitelist");

            for (String s : json.keySet()) {
                whitelist.put(
                        s,
                        PermanentCertificate.build(json.getJSONObject(s))
                );
            }
        });
    }

    public static void initNotes() {
        notes = new JSONObject();

        EntrustEnvironment.trys(() -> notes = config.getJSONObject("notes"));
    }

    public static void initBan() {
        banned.clear();

        EntrustEnvironment.trys(
                () -> {
                    JSONObject json = config.getJSONObject("banned");

                    for (String s : json.keySet()) {
                        banned.put(
                                s,
                                Certificate.build(json.getJSONObject(s))
                        );
                    }
                },
                Throwable::printStackTrace
        );
    }

    public static void addScoreboard(MinecraftServer server, Text displayName, String id) {
        ServerScoreboard scoreboard = server.getScoreboard();
        if (scoreboard.containsObjective(id)) {
            scoreboard.removeObjective(scoreboard.getObjective(id));
        }
        scoreboard.addObjective(
                id,
                ScoreboardCriterion.DUMMY,
                displayName,
                ScoreboardCriterion.DUMMY.getDefaultRenderType()
        );
    }

    public static String getServerLevelPath(MinecraftServer server) {
        return (server.isDedicated() ? "" : "saves/") + getServerLevelNamePath(server);
    }

    public static String getServerLevelNamePath(MinecraftServer server) {
        return getServerLevelName(server) + "/";
    }

    public static String getServerLevelName(MinecraftServer server) {
        return ((MinecraftServerInterface) server).getSession()
                                                  .getDirectoryName();
    }

    public static String getApply(CommandContext<ServerCommandSource> source) {
        return getApply(SimpleCommandOperation.getServer(source));
    }

    public static String getApply(MinecraftServer server) {
        return ((MinecraftServerInterface) server).getSession()
                                                  .getDirectoryName() + "/";
    }

    public static String getApply() {
        return getApply(server);
    }

    public static void sendMessageToAllPlayer(Text message, boolean actionBar) {
        sendMessageToAllPlayer(
                server,
                message,
                actionBar
        );
    }

    public static void sendMessageToAllPlayer(MinecraftServer server, Text message, boolean actionBar) {
        for (ServerPlayerEntity player : server.getPlayerManager()
                                               .getPlayerList())
            sendMessage(
                    player,
                    message,
                    actionBar
            );
    }

    public static void sendMessage(ServerPlayerEntity player, Text message, boolean actionBar) {
        player.sendMessage(
                message,
                actionBar
        );
    }

    public static String getPlayerModMdoName(ServerPlayerEntity player) {
        return EntrustEnvironment.trys(() -> loginUsers.getUser(player)
                                                       .getModmdoName());
    }

    public static void defaultConfig() {
        config.setIfNoExist(
                "default_language",
                Language.EN_US
        );
        config.setIfNoExist(
                "here_command",
                true
        );
        config.setIfNoExist(
                "cava",
                true
        );
        config.setIfNoExist(
                "secure_enchant",
                true
        );
        config.setIfNoExist(
                "modmdo_whitelist",
                false
        );
        config.setIfNoExist(
                "reject_reconnect",
                true
        );
        config.setIfNoExist(
                "time_active",
                true
        );
        config.setIfNoExist(
                "checker_time_limit",
                3000
        );
        config.setIfNoExist(
                "enchantment_clear_if_level_too_high",
                false
        );
        config.setIfNoExist(
                "reject_no_fall_chest",
                true
        );
        config.setIfNoExist(
                "whitelist_only_id",
                false
        );
        config.setIfNoExist(
                "compatible_online_mode",
                true
        );
    }

    public static void saveVariables(Temporary action) {
        action.apply();
        saveVariables();
    }

    public static void saveVariables() {
        config.set(
                "here_command",
                enableHereCommand
        );
        config.set(
                "secure_enchant",
                enableSecureEnchant
        );
        config.set(
                "reject_reconnect",
                enableRejectReconnect
        );
        config.set(
                "time_active",
                timeActive
        );
        config.set(
                "modmdo_whitelist",
                modmdoWhitelist
        );
        config.set(
                "notes",
                notes
        );

        if (modMdoType == ModMdoType.SERVER) {
            EntrustEnvironment.trys(() -> {
                JSONObject json = new JSONObject();
                for (String s : whitelist.keySet()) {
                    json.put(
                            s,
                            whitelist.get(s)
                                     .toJSONObject()
                    );
                }
                config.set(
                        "whitelist",
                        json
                );
            });

            EntrustEnvironment.trys(() -> {
                JSONObject json = new JSONObject();
                for (String s : banned.keySet()) {
                    json.put(
                            s,
                            banned.get(s)
                                  .toJSONObject()
                    );
                }
                config.set(
                        "banned",
                        json
                );
            });
        }
    }

    public static Language getLanguage() {
        return Language.ofs(config.getString("default_language"));
    }

    public static void handleTemporaryWhitelist() {
        for (TemporaryCertificate wl : temporaryStation.values()) {
            if (! wl.isValid()) {
                temporaryStation.remove(wl.getName());
            }
        }
    }

    public static void handleTemporaryBan() {
        for (String name : banned.keySet()) {
            Certificate ban = banned.get(name);
            if (ban == null) {
                banned.remove(name);
                continue;
            }
            if (ban instanceof TemporaryCertificate temp) {
                if (! temp.isValid()) {
                    banned.remove(name);
                }
            }
        }
    }

    public static boolean isActive() {
        return EntrustEnvironment.trys(
                () -> extras.isActive(EXTRA_ID),
                () -> false
        );
    }

    public static boolean notWhitelist(ServerPlayerEntity player) {
        return ! hasWhitelist(player);
    }

    public static boolean hasWhitelist(ServerPlayerEntity player) {
        try {
            if (temporaryInvite.containsName(EntityUtil.getName(player))) {
                if (temporaryInvite.get(EntityUtil.getName(player))
                                   .getMillions() == - 1) {
                    temporaryInvite.remove(EntityUtil.getName(player));
                    player.networkHandler.connection.send(new DisconnectS2CPacket(minecraftTextFormat.format(
                                                                                                             loginUsers.getUser(player),
                                                                                                             "modmdo.invite.canceled"
                                                                                                     )
                                                                                                     .text()));
                    player.networkHandler.connection.disconnect(minecraftTextFormat.format(
                                                                                           loginUsers.getUser(player),
                                                                                           "modmdo.invite.canceled"
                                                                                   )
                                                                                   .text());
                    return true;
                }
                if (! temporaryInvite.get(EntityUtil.getName(player))
                                     .isValid()) {
                    temporaryInvite.remove(EntityUtil.getName(player));
                    player.networkHandler.connection.send(new DisconnectS2CPacket(minecraftTextFormat.format(
                                                                                                             loginUsers.getUser(player),
                                                                                                             "modmdo.invite.expired"
                                                                                                     )
                                                                                                     .text()));
                    player.networkHandler.connection.disconnect(minecraftTextFormat.format(
                                                                                           loginUsers.getUser(player),
                                                                                           "modmdo.invite.expired"
                                                                                   )
                                                                                   .text());
                }
                return true;
            }
            if (config.getBoolean("whitelist_only_id")) {
                return whitelist.getFromId(loginUsers.getUser(player)
                                                     .getIdentifier()) != null;
            }
            switch (whitelist.get(EntityUtil.getName(player))
                             .getRecorde()
                             .type()) {
                case IDENTIFIER -> {
                    return whitelist.get(EntityUtil.getName(player))
                                    .getRecorde()
                                    .getUniqueId()
                                    .equals(loginUsers.getUser(player)
                                                      .getIdentifier());
                }
                case UUID -> {
                    if (Objects.requireNonNull(player.getServer())
                               .isOnlineMode()) {
                        if (! player.getUuid()
                                    .equals(whitelist.get(EntityUtil.getName(player))
                                                     .getRecorde()
                                                     .getUuid())) {
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
        if (config.getBoolean("modmdo_whitelist")) {
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
            switch (banned.get(EntityUtil.getName(player))
                          .getRecorde()
                          .type()) {
                case IDENTIFIER -> {
                    if (banned.get(EntityUtil.getName(player))
                              .getRecorde()
                              .getUniqueId()
                              .equals("")) {
                        return false;
                    }
                }
                case UUID -> {
                    if (! player.getUuid()
                                .equals(banned.get(EntityUtil.getName(player))
                                              .getRecorde()
                                              .getUuid())) {
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
