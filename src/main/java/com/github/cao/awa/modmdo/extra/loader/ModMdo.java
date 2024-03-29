package com.github.cao.awa.modmdo.extra.loader;

import com.alibaba.fastjson2.*;
import com.github.cao.awa.modmdo.commands.*;
import com.github.cao.awa.modmdo.config.*;
import com.github.cao.awa.modmdo.event.trigger.*;
import com.github.cao.awa.modmdo.event.variable.*;
import com.github.cao.awa.modmdo.lang.*;
import com.github.cao.awa.modmdo.resource.loader.*;
import com.github.cao.awa.modmdo.security.certificate.*;
import com.github.cao.awa.modmdo.service.handler.certificate.nosql.lilac.*;
import com.github.cao.awa.modmdo.service.handler.text.*;
import com.github.cao.awa.modmdo.usr.*;
import com.github.cao.awa.modmdo.utils.entity.*;
import com.github.cao.awa.modmdo.utils.file.*;
import com.github.cao.awa.modmdo.utils.io.*;
import com.github.cao.awa.modmdo.utils.text.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import net.minecraft.network.*;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;

import java.io.*;

import static com.github.cao.awa.modmdo.ModMdoStdInitializer.*;
import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

public class ModMdo extends ModMdoExtra<ModMdo> {
    private MinecraftServer server;

    private int ticks;

    public void init() {
        String path = getServerLevelPath(getServer()) + "modmdo/configs";
        FileUtil.mkdirs(new File(path));
        File file = new File(path + "/compress.txt");
        if (! file.isFile()) {
            EntrustEnvironment.trys(file::createNewFile);
        }
        boolean compress = EntrustEnvironment.trys(
                () -> Boolean.parseBoolean(IOUtil.read(new BufferedInputStream(new FileInputStream(path + "/compress.txt")))),
                () -> false
        );
        config = new DiskConfigUtil(
                ENTRUST,
                path,
                "modmdo",
                compress
        );

        allDefault();
        defaultConfig();

        loadDatabaseConfig(path);

        EntrustEnvironment.trys(() -> initModMdoVariables(modMdoType));

        whitelistsService = new LilacCertificateService<>(
                path + "/certificates/whitelists",
                "Whitelists"
        );

        stationService = new LilacCertificateService<>(
                null,
                "Stations"
        );

        invitesService = new LilacCertificateService<>(
                null,
                "Invites"
        );

        bans = new LilacCertificateService<>(
                path + "/certificates/bans",
                "Bans"
        );

        saveVariables();
    }

    public static void loadDatabaseConfig(String path) {
        database.init(() -> EntrustEnvironment.receptacle(receptacle -> {
            File config = new File(path + "/certificate-db.conf");
            if (! config.isFile()) {
                LOGGER.warn("ModMdo certificate database config not found, generating default config");

                EntrustEnvironment.trys(() -> config.getParentFile()
                                                    .mkdirs());

                receptacle.set(IOUtil.read(ResourcesLoader.getResource("configs/certificate-db-default.conf")));

                EntrustEnvironment.operation(
                        new BufferedWriter(new FileWriter(config)),
                        writer -> IOUtil.write(
                                writer,
                                receptacle.get()
                        )
                );
            }

            if (receptacle.get() == null) {
                receptacle.set(IOUtil.read(new BufferedReader(new FileReader(config))));
            }
        }));
    }

    public MinecraftServer getServer() {
        return server;
    }

    public void setServer(MinecraftServer server) {
        this.server = server;
    }

    public void initCommand() {
        EntrustEnvironment.trys(
                () -> {
                    new TestCommand().register();
                    new TemporaryCommand().register();
                    new ModMdoCommand().register();
                    new NoteCommand().register();
                },
                ex -> LOGGER.debug(
                        "Failed load ModMdo commands",
                        ex
                )
        );
    }

    public void initStaticCommand() {
    }

    public void initEvent() {
        triggerBuilder = new ModMdoTriggerBuilder();

        textFormatService = new TextFormatService();

        EntrustEnvironment.trys(() -> {
            new File(getServerLevelPath(getServer()) + "/modmdo/resources/events/").mkdirs();

            EntrustEnvironment.tryFor(
                    EntrustEnvironment.getNotNull(
                            new File(getServerLevelPath(getServer()) + "/modmdo/resources/events/").listFiles(),
                            new File[0]
                    ),
                    file -> {
                        EntrustEnvironment.trys(
                                () -> {
                                    if (file.isFile()) {
                                        triggerBuilder.register(
                                                JSONObject.parseObject(IOUtil.read(new BufferedReader(new FileReader(file)))),
                                                file
                                        );
                                        LOGGER.info("Registered event: " + file.getPath());
                                    }
                                },
                                ex -> {
                                    LOGGER.warn(
                                            "Failed register event: " + file.getPath(),
                                            ex
                                    );
                                }
                        );
                    }
            );
        });

        VARIABLES.clear();
        variableBuilder = new ModMdoVariableBuilder();
        EntrustEnvironment.trys(() -> {
            new File(getServerLevelPath(getServer()) + "/modmdo/resources/persistent/").mkdirs();

            EntrustEnvironment.tryFor(
                    EntrustEnvironment.getNotNull(
                            new File(getServerLevelPath(getServer()) + "/modmdo/resources/persistent/").listFiles(),
                            new File[0]
                    ),
                    file -> {
                        EntrustEnvironment.notNull(
                                variableBuilder.build(
                                        file,
                                        JSONObject.parseObject(IOUtil.read(new BufferedReader(new FileReader(file))))
                                ),
                                v -> {
                                    VARIABLES.put(
                                            v.getName(),
                                            v
                                    );
                                }
                        );
                    }
            );
        });

        Resource<String> resource = new Resource<>();
        resource.set(
                Language.ZH_CN.getName(),
                "assets/modmdo/lang/zh_cn.json"
        );
        resource.set(
                Language.EN_US.getName(),
                "assets/modmdo/lang/en_us.json"
        );

        EntrustEnvironment.trys(() -> {
            new File(getServerLevelPath(getServer()) + "/modmdo/resources/lang/").mkdirs();

            EntrustEnvironment.tryFor(
                    EntrustEnvironment.getNotNull(
                            new File(getServerLevelPath(getServer()) + "/modmdo/resources/lang/").listFiles(),
                            new File[0]
                    ),
                    file -> {
                        if (file.getName()
                                .startsWith("dictionary_")) {
                            EntrustEnvironment.trys(
                                    () -> {
                                        resource.set(
                                                file.getName()
                                                    .substring(
                                                            11,
                                                            file.getName()
                                                                .indexOf(".")
                                                    ),
                                                file.getAbsolutePath()
                                        );
                                    },
                                    ex -> {
                                        resource.set(
                                                file.getName(),
                                                file.getAbsolutePath()
                                        );
                                    }
                            );
                        } else {
                            Language lang = Language.ofs(file.getName());
                            if (lang != null) {
                                resource.set(
                                        lang.getName(),
                                        file.getAbsolutePath()
                                );
                            }
                        }
                    }
            );
        });
        textFormatService.attach(resource);

        event.clientSetting.register(
                event -> {
                    loginUsers.getUser(event.getPlayer())
                              .setLanguage(Language.ofs(event.getLanguage()));
                    User user = loginUsers.getUser(event.getPlayer());
                    if (user.getMessage() != null) {
                        event.getPlayer()
                             .sendMessage(
                                     textFormatService.format(
                                                              new Dictionary(user.getLanguage()
                                                                                 .getName()),
                                                              TextUtil.translatable(user.getMessage())
                                                      )
                                                      .text(),
                                     false
                             );
                        user.setMessage(null);
                    }
                },
                this,
                "SettingClient"
        );

        event.gameTickStart.register(
                event -> {
                    PlayerManager players = server.getPlayerManager();

                    EntrustEnvironment.trys(() -> {
                        for (ServerPlayerEntity player : players.getPlayerList()) {
                            ClientConnection connection = player.networkHandler.getConnection();

                            if (connection.getAddress() != null) {
                                if (! connection.isOpen()) {
                                    // try remove
                                    server.getPlayerManager()
                                          .remove(player);

                                    // force remove
                                    server.getPlayerManager()
                                          .getPlayerList()
                                          .remove(player);
                                    return;
                                }

                                if (modmdoWhitelist) {
                                    if (notWhitelist(player) || ! loginUsers.getUser(player)
                                                                            .isLogged()) {
                                        connection.send(new DisconnectS2CPacket(TextUtil.translatable("multiplayer.disconnect.not_whitelisted")
                                                                                        .text()));
                                        connection.disconnect(TextUtil.translatable("multiplayer.disconnect.not_whitelisted")
                                                                      .text());
                                        return;
                                    }
                                    if (hasBan(player)) {
                                        Certificate ban = bans.get(EntityUtil.getName(player));
                                        if (ban instanceof TemporaryCertificate temporary) {
                                            String remaining = temporary.formatRemaining();
                                            connection.send(new DisconnectS2CPacket(textFormatService.format(
                                                                                                             new Dictionary(ban.getLanguage()),
                                                                                                             "multiplayer.disconnect.banned-time-limited",
                                                                                                             remaining
                                                                                                     )
                                                                                                     .text()));
                                            connection.disconnect(textFormatService.format(
                                                                                           new Dictionary(ban.getLanguage()),
                                                                                           "multiplayer.disconnect.banned-time-limited",
                                                                                           remaining
                                                                                   )
                                                                                   .text());
                                        } else {
                                            connection.send(new DisconnectS2CPacket(textFormatService.format(
                                                                                                             new Dictionary(ban.getLanguage()),
                                                                                                             "multiplayer.disconnect.banned-indefinite"
                                                                                                     )
                                                                                                     .text()));
                                            connection.disconnect(textFormatService.format(
                                                                                           new Dictionary(ban.getLanguage()),
                                                                                           "multiplayer.disconnect.banned-indefinite"
                                                                                   )
                                                                                   .text());
                                        }
                                    }
                                }
                            }
                        }

                    });

                    //                    if (ddosRecording != null) {
                    //                        if (ticks++ > 200) {
                    //                            ticks = 0;
                    //                            DdosAttackRecorder.LOGGER.info("----DDOS INFO----");
                    //                            DdosAttackRecorder.LOGGER.info("Times(Seconds): " + ddosRecording.getTimes());
                    //                            DdosAttackRecorder.LOGGER.info("Total attacks: " + ddosRecording.getAttacks()
                    //                                                                                            .get(ddosRecording.getAttacks()
                    //                                                                                                              .size() - 1));
                    //                            DdosAttackRecorder.LOGGER.info("Attacks per second: " + ddosRecording.average());
                    //                            DdosAttackRecorder.LOGGER.info("In second attacks: " + ddosRecording.getOccurring());
                    //                        }
                    //                        if (ticks % 20 == 0) {
                    //                            ddosRecording.occursAhead();
                    //                        }
                    //                    }
                },
                this,
                "HandlePlayers"
        );
    }
}
