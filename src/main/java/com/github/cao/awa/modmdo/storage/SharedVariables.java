package com.github.cao.awa.modmdo.storage;

import com.alibaba.fastjson2.*;
import com.github.cao.awa.modmdo.attack.ddos.recorder.*;
import com.github.cao.awa.modmdo.commands.*;
import com.github.cao.awa.modmdo.config.*;
import com.github.cao.awa.modmdo.develop.clazz.*;
import com.github.cao.awa.modmdo.event.*;
import com.github.cao.awa.modmdo.event.trigger.*;
import com.github.cao.awa.modmdo.event.variable.*;
import com.github.cao.awa.modmdo.extra.loader.*;
import com.github.cao.awa.modmdo.lang.Language;
import com.github.cao.awa.modmdo.mixins.server.*;
import com.github.cao.awa.modmdo.security.certificate.*;
import com.github.cao.awa.modmdo.security.key.*;
import com.github.cao.awa.modmdo.server.login.*;
import com.github.cao.awa.modmdo.service.handler.certificate.*;
import com.github.cao.awa.modmdo.service.handler.text.*;
import com.github.cao.awa.modmdo.supplier.*;
import com.github.cao.awa.modmdo.type.*;
import com.github.cao.awa.modmdo.usr.*;
import com.github.cao.awa.modmdo.utils.command.*;
import com.github.cao.awa.modmdo.utils.entity.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.receptacle.*;
import com.mojang.brigadier.context.*;
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
import org.jetbrains.annotations.*;

import java.text.*;
import java.util.*;

public class SharedVariables {
    public static final Logger LOGGER = LogManager.getLogger("ModMdo");
    public static final byte[] MODMDO_NONCE = "MODMDO:SERVER_NONCE_!+[RD]".getBytes();
    public static final byte[] MODMDO_NONCE_HEAD = "MODMDO:SERVER_NONCE_!+".getBytes();
    public static final String TEST_MODMDO_VERSION = "10410004";
    public static final String VERSION_ID = "1.0.45";
    public static final String SUFFIX = "-Debug";
    public static final String MODMDO_VERSION_NAME = VERSION_ID + SUFFIX;
    public static final String RELEASE_TIME = "UTC+8 2022.12.18";
    public static final String ENTRUST = "ModMdo";
    public static final UUID EXTRA_ID = UUID.fromString("1a6dbe1a-fea8-499f-82d1-cececcf78b7c");
    public static final NumberFormat FRACTION_DIGITS_2 = NumberFormat.getNumberInstance();
    public static final NumberFormat FRACTION_DIGITS_1 = NumberFormat.getNumberInstance();
    public static final NumberFormat FRACTION_DIGITS_0 = NumberFormat.getNumberInstance();
    public static final Identifier CHECKING_CHANNEL = new Identifier("modmdo:check");
    public static final Identifier LOGIN_CHANNEL = new Identifier("modmdo:login");
    public static final Identifier INFO_CHANNEL = new Identifier("modmdo:info");
    public static final Identifier SERVER_CHANNEL = new Identifier("modmdo:server");
    public static final Identifier CLIENT_CHANNEL = new Identifier("modmdo:client");
    public static final Identifier TOKEN_CHANNEL = new Identifier("modmdo:token");
    public static final Map<String, ModMdoPersistent<?>> VARIABLES = new Object2ObjectOpenHashMap<>();
    public static final List<ServerPlayerEntity> FORCE = new ObjectArrayList<>();
    public static final SecureKeys SECURE_KEYS = new SecureKeys();
    public static final ClazzScanner EXTRA_AUTO = new ClazzScanner(ModMdoExtra.class);
    public static final List<ClientConnection> connections = new ObjectArrayList<>();
    public static final List<DdosAttackRecorder> ddosAttackRecorders = new ObjectArrayList<>();
    public static String identifier;
    public static boolean enableHereCommand = true;
    public static boolean enableRejectReconnect = true;
    public static boolean modmdoWhitelist = false;
    public static Map<String, Long> loginTimedOut = new Object2ObjectOpenHashMap<>();
    public static Users rejectUsers;
    public static Users loginUsers;
    public static DiskConfigUtil config;
    public static DiskConfigUtil staticConfig;
    public static MinecraftServer server;
    public static ModMdoType modMdoType = ModMdoType.NONE;
    public static int itemDespawnAge = 6000;
    public static ServerLogin serverLogin = new ServerLogin();
    public static CertificateService<PermanentCertificate> whitelistsService;
    public static CertificateService<TemporaryCertificate> stationService;
    public static CertificateService<TemporaryCertificate> invitesService;
    public static CertificateService<Certificate> bans;
    public static TextFormatService textFormatService;
    public static ModMdoExtraLoader extras;
    public static boolean loaded = false;
    public static boolean debug = false;
    public static boolean testing = false;
    public static ModMdoCommandRegister commandRegister;
    public static ModMdoEventTracer event = new ModMdoEventTracer();
    public static ModMdoTriggerBuilder triggerBuilder = new ModMdoTriggerBuilder();
    public static ModMdoVariableBuilder variableBuilder = new ModMdoVariableBuilder();
    public static JSONObject notes = new JSONObject();

    public static final Configure database = new Configure(SupplierTemplates.emptyString());

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

        enableHereCommand = true;
        enableRejectReconnect = true;
        rejectUsers = new Users();
        loginUsers = new Users();
        itemDespawnAge = 6000;

        FORCE.clear();
    }

    public static void initNotes() {
        notes = new JSONObject();

        EntrustEnvironment.trys(() -> notes = config.getJSONObject("notes"));
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
        return EntrustEnvironment.trys(
                () -> loginUsers.getUser(player)
                                .getModmdoName(),
                ex -> null
        );
    }

    public static void defaultConfig() {
        config.setIfNoExist(
                "default_language",
                Language.EN_US
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
                "checker_time_limit",
                3000
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
                "reject_reconnect",
                enableRejectReconnect
        );
        config.set(
                "modmdo_whitelist",
                modmdoWhitelist
        );
        config.set(
                "notes",
                notes
        );
    }

    public static Language getLanguage() {
        return Language.ofs(config.getString("default_language"));
    }

    public static void handleTemporaryWhitelist() {
        for (Certificate wl : stationService.values()) {
            if (! wl.isValid()) {
                stationService.delete(wl.getName());
            }
        }
    }

    public static void handleTemporaryBan() {
        for (String name : bans.keys()) {
            Certificate ban = bans.get(name);
            if (ban == null) {
                bans.delete(name);
                continue;
            }
            if (ban instanceof TemporaryCertificate temp) {
                if (! temp.isValid()) {
                    bans.delete(name);
                }
            }
        }
    }

    public static boolean notWhitelist(ServerPlayerEntity player) {
        return ! hasWhitelist(player);
    }

    public static boolean hasWhitelist(ServerPlayerEntity player) {
        try {
            ClientConnection connection = player.networkHandler.getConnection();
            String name = EntityUtil.getName(player);
            User user = loginUsers.getUser(player);
            if (invitesService.containsName(name)) {
                if (invitesService.get(name)
                                  .getMillions() == - 1) {
                    invitesService.delete(name);
                    connection.send(new DisconnectS2CPacket(textFormatService.format(
                                                                                     user,
                                                                                     "modmdo.invite.canceled"
                                                                             )
                                                                             .text()));
                    connection.disconnect(textFormatService.format(
                                                                   user,
                                                                   "modmdo.invite.canceled"
                                                           )
                                                           .text());
                    return true;
                }
                if (! invitesService.get(name)
                                    .isValid()) {
                    invitesService.delete(name);
                    connection.send(new DisconnectS2CPacket(textFormatService.format(
                                                                                     user,
                                                                                     "modmdo.invite.expired"
                                                                             )
                                                                             .text()));
                    connection.disconnect(textFormatService.format(
                                                                   user,
                                                                   "modmdo.invite.expired"
                                                           )
                                                           .text());
                }
                return true;
            }
            if (config.getBoolean("whitelist_only_id")) {
                return whitelistsService.getFromId(user.getIdentifier()) != null;
            }
            switch (whitelistsService.get(name)
                                     .getRecorde()
                                     .type()) {
                case IDENTIFIER -> {
                    return validIdentifierWhitelist(name);
                }
                case UUID -> {
                    if (Objects.requireNonNull(player.getServer())
                               .isOnlineMode()) {
                        return validUuidWhitelist(
                                EntityUtil.getName(player),
                                player.getUuid()
                        );

                    } else {
                        return false;
                    }
                }
                case MULTIPLE -> {
                    return validIdentifierWhitelist(name) || (Objects.requireNonNull(player.getServer())
                                                                     .isOnlineMode() && validUuidWhitelist(
                            name,
                            player.getUuid()
                    ));
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean validIdentifierWhitelist(@NotNull String name) {
        return EntrustEnvironment.get(
                () -> whitelistsService.get(name)
                                       .getRecorde()
                                       .getUniqueId()
                                       .equals(loginUsers.getUserFromName(name)
                                                         .getIdentifier()),
                false
        );
    }

    public static boolean validUuidWhitelist(@NotNull String name, @NotNull UUID uuid) {
        return uuid.equals(whitelistsService.get(name)
                                            .getRecorde()
                                            .getUuid());
    }

    public static boolean handleBanned(ServerPlayerEntity player) {
        if (config.getBoolean("modmdo_whitelist")) {
            if (hasBan(player)) {
                String name = EntityUtil.getName(player);
                if (bans.get(name) instanceof TemporaryCertificate certificate) {
                    if (certificate.isValid()) {
                        return true;
                    } else {
                        bans.delete(name);
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
            Certificate banned = bans.get(EntityUtil.getName(player));
            if (banned == null) {
                return false;
            }
            LoginRecorde recorde = banned.getRecorde();
            switch (recorde.type()) {
                case IDENTIFIER -> {
                    if (recorde.getUniqueId()
                               .equals("")) {
                        return false;
                    }
                }
                case UUID -> {
                    if (! player.getUuid()
                                .equals(recorde.getUuid())) {
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
