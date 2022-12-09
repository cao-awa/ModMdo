package com.github.cao.awa.modmdo.extra.loader;

import com.github.cao.awa.modmdo.attack.ddos.recorder.*;
import com.github.cao.awa.modmdo.commands.*;
import com.github.cao.awa.modmdo.config.*;
import com.github.cao.awa.modmdo.event.trigger.*;
import com.github.cao.awa.modmdo.event.variable.*;
import com.github.cao.awa.modmdo.format.console.*;
import com.github.cao.awa.modmdo.format.minecraft.*;
import com.github.cao.awa.modmdo.lang.*;
import com.github.cao.awa.modmdo.resourceLoader.*;
import com.github.cao.awa.modmdo.security.certificate.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.usr.*;
import com.github.cao.awa.modmdo.utils.entity.*;
import com.github.cao.awa.modmdo.utils.io.*;
import com.github.cao.awa.modmdo.utils.text.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;
import org.json.*;

import java.io.*;

import static com.github.cao.awa.modmdo.ModMdoStdInitializer.*;
import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

public class ModMdo extends ModMdoExtra<ModMdo> {
    private MinecraftServer server;

    private int ticks;

    public void init() {
        String path = getServerLevelPath(getServer()) + "modmdo/configs";
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

        EntrustEnvironment.trys(() -> initModMdoVariables(modMdoType));

        saveVariables();
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
                    new HereCommand().register();
                    new DimensionHereCommand().register();
                    new TestCommand().register();
                    new TemporaryCommand().register();
                    new ModMdoCommand().register();
                    new NoteCommand().register();
                    new BenchmarkCommand().register();
                },
                ex -> LOGGER.debug("Failed load ModMdo commands", ex)
        );
    }

    public void initStaticCommand() {
    }

    public void initEvent() {
        triggerBuilder = new ModMdoTriggerBuilder();

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
                                                new JSONObject(IOUtil.read(new BufferedReader(new FileReader(file)))),
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
                                        new JSONObject(IOUtil.read(new BufferedReader(new FileReader(file))))
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
        SharedVariables.consoleTextFormat = new ConsoleTextFormat(resource);
        SharedVariables.minecraftTextFormat = new MinecraftTextFormat(resource);

        event.clientSetting.register(
                event -> {
                    loginUsers.getUser(event.getPlayer())
                              .setLanguage(Language.ofs(event.getLanguage()));
                    User user = loginUsers.getUser(event.getPlayer());
                    if (user.getMessage() != null) {
                        event.getPlayer()
                             .sendMessage(
                                     minecraftTextFormat.format(
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
                            if (player.networkHandler.connection.getAddress() != null) {
                                if (! player.networkHandler.connection.isOpen()) {
                                    // try remove
                                    server.getPlayerManager()
                                          .remove(player);

                                    // force remove
                                    server.getPlayerManager()
                                          .getPlayerList()
                                          .remove(player);
                                }

                                if (modmdoWhitelist) {
                                    if (notWhitelist(player) || ! loginUsers.getUser(player)
                                                                            .isLogged()) {
                                        player.networkHandler.connection.send(new DisconnectS2CPacket(TextUtil.translatable("multiplayer.disconnect.not_whitelisted")
                                                                                                              .text()));
                                        player.networkHandler.connection.disconnect(TextUtil.translatable("multiplayer.disconnect.not_whitelisted")
                                                                                            .text());

                                    }
                                    if (hasBan(player)) {
                                        Certificate ban = banned.get(EntityUtil.getName(player));
                                        if (ban instanceof TemporaryCertificate temporary) {
                                            String remaining = temporary.formatRemaining();
                                            player.networkHandler.connection.send(new DisconnectS2CPacket(minecraftTextFormat.format(
                                                                                                                                     new Dictionary(ban.getLastLanguage()),
                                                                                                                                     "multiplayer.disconnect.banned-time-limited",
                                                                                                                                     remaining
                                                                                                                             )
                                                                                                                             .text()));
                                            player.networkHandler.connection.disconnect(minecraftTextFormat.format(
                                                                                                                   new Dictionary(ban.getLastLanguage()),
                                                                                                                   "multiplayer.disconnect.banned-time-limited",
                                                                                                                   remaining
                                                                                                           )
                                                                                                           .text());
                                        } else {
                                            player.networkHandler.connection.send(new DisconnectS2CPacket(minecraftTextFormat.format(
                                                                                                                                     new Dictionary(ban.getLastLanguage()),
                                                                                                                                     "multiplayer.disconnect.banned-indefinite"
                                                                                                                             )
                                                                                                                             .text()));
                                            player.networkHandler.connection.disconnect(minecraftTextFormat.format(
                                                                                                                   new Dictionary(ban.getLastLanguage()),
                                                                                                                   "multiplayer.disconnect.banned-indefinite"
                                                                                                           )
                                                                                                           .text());
                                        }
                                    }
                                }
                            }
                        }

                    });

                    if (ddosRecording != null) {
                        if (ticks++ > 200) {
                            ticks = 0;
                            DdosAttackRecorder.LOGGER.info("----DDOS INFO----");
                            DdosAttackRecorder.LOGGER.info("Times(Seconds): " + ddosRecording.getTimes());
                            DdosAttackRecorder.LOGGER.info("Total attacks: " + ddosRecording.getAttacks()
                                                                                            .get(ddosRecording.getAttacks()
                                                                                                              .size() - 1));
                            DdosAttackRecorder.LOGGER.info("Attacks per second: " + ddosRecording.average());
                            DdosAttackRecorder.LOGGER.info("In second attacks: " + ddosRecording.getOccurring());
                        }
                        if (ticks % 20 == 0) {
                            ddosRecording.occursAhead();
                        }
                    }
                },
                this,
                "HandlePlayers"
        );
    }
}
