package com.github.cao.awa.modmdo.extra.loader;

import com.github.cao.awa.modmdo.certificate.*;
import com.github.cao.awa.modmdo.commands.*;
import com.github.cao.awa.modmdo.event.trigger.*;
import com.github.cao.awa.modmdo.event.variable.*;
import com.github.cao.awa.modmdo.format.console.*;
import com.github.cao.awa.modmdo.format.minecraft.*;
import com.github.cao.awa.modmdo.lang.*;
import com.github.cao.awa.modmdo.network.forwarder.process.*;
import com.github.cao.awa.modmdo.resourceLoader.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.utils.entity.*;
import com.github.cao.awa.modmdo.utils.file.reads.*;
import com.github.cao.awa.modmdo.utils.text.*;
import com.github.cao.awa.modmdo.utils.usr.*;
import com.github.zhuaidadaya.rikaishinikui.handler.config.*;
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

    public void init() {
        String path = getServerLevelPath(getServer()) + "modmdo/configs";
        File file = new File(path + "/compress.txt");
        if (!file.isFile()) {
            EntrustExecution.tryTemporary(file::createNewFile);
        }
        boolean compress = EntrustParser.trying(() -> Boolean.parseBoolean(FileReads.strictRead(new BufferedInputStream(new FileInputStream(path + "/compress.txt")))), () -> false);
        config = new DiskObjectConfigUtil(entrust, path, "modmdo", compress);

        allDefault();
        defaultConfig();

        try {
            initModMdoVariables(modMdoType);
        } catch (Exception e) {

        }

        saveVariables();
    }

    public MinecraftServer getServer() {
        return server;
    }

    public void setServer(MinecraftServer server) {
        this.server = server;
    }

    public void initCommand() {
        try {
            new HereCommand().register();
            new DimensionHereCommand().register();
            new TestCommand().register();
            new TemporaryCommand().register();
            new ModMdoCommand().register();
        } catch (Exception e) {

        }
    }

    public void initStaticCommand() {
    }

    public void initEvent() {
        triggerBuilder = new ModMdoTriggerBuilder();

        EntrustExecution.tryTemporary(() -> {
            new File(getServerLevelPath(getServer()) + "/modmdo/resources/events/").mkdirs();

            EntrustExecution.tryFor(EntrustParser.getNotNull(new File(getServerLevelPath(getServer()) + "/modmdo/resources/events/").listFiles(), new File[0]), file -> {
                EntrustExecution.tryTemporary(() -> {
                    if (file.isFile()) {
                        triggerBuilder.register(new JSONObject(FileReads.read(new BufferedReader(new FileReader(file)))), file);
                        LOGGER.info("Registered event: " + file.getPath());
                    }
                }, ex -> {
                    LOGGER.warn("Failed register event: " + file.getPath(), ex);
                });
            });
        });

        variables.clear();
        variableBuilder = new ModMdoVariableBuilder();
        EntrustExecution.tryTemporary(() -> {
            new File(getServerLevelPath(getServer()) + "/modmdo/resources/persistent/").mkdirs();

            EntrustExecution.tryFor(EntrustParser.getNotNull(new File(getServerLevelPath(getServer()) + "/modmdo/resources/persistent/").listFiles(), new File[0]), file -> {
                EntrustExecution.notNull(variableBuilder.build(file, new JSONObject(FileReads.read(new BufferedReader(new FileReader(file))))), v -> {
                    variables.put(v.getName(), v);
                });
            });
        });

        Resource<String> resource = new Resource<>();
        resource.set(Language.ZH_CN.getName(), "assets/modmdo/lang/zh_cn.json");
        resource.set(Language.EN_US.getName(), "assets/modmdo/lang/en_us.json");

        EntrustExecution.tryTemporary(() -> {
            new File(getServerLevelPath(getServer())  + "/modmdo/resources/lang/").mkdirs();

            EntrustExecution.tryFor(EntrustParser.getNotNull(new File(getServerLevelPath(getServer())  + "/modmdo/resources/lang/").listFiles(), new File[0]), file -> {
                if (file.getName().startsWith("dictionary_")) {
                    EntrustExecution.tryTemporary(() -> {
                        resource.set(file.getName().substring(11, file.getName().indexOf(".")), file.getAbsolutePath());
                    }, ex -> {
                        resource.set(file.getName(), file.getAbsolutePath());
                    });
                } else {
                    Language lang = Language.ofs(file.getName());
                    if (lang != null) {
                        resource.set(lang.getName(), file.getAbsolutePath());
                    }
                }
            });
        });
        SharedVariables.consoleTextFormat = new ConsoleTextFormat(resource);
        SharedVariables.minecraftTextFormat = new MinecraftTextFormat(resource);

        event.clientSetting.register(event -> {
            loginUsers.getUser(event.getPlayer()).setLanguage(Language.ofs(event.getLanguage()));
            User user = loginUsers.getUser(event.getPlayer());
            if (user.getMessage() != null) {
                event.getPlayer().sendMessage(minecraftTextFormat.format(new Dictionary(user.getLanguage().getName()), TextUtil.translatable(user.getMessage())).text(), false);
                user.setMessage(null);
            }
        }, this,"SettingClient");

        event.gameTickStart.register(event -> {
            PlayerManager players = server.getPlayerManager();

            EntrustExecution.tryTemporary(() -> {
                for (ServerPlayerEntity player : players.getPlayerList()) {
                    if (modmdoWhitelist) {
                        if (!hasWhitelist(player)) {
                            player.networkHandler.connection.send(new DisconnectS2CPacket(TextUtil.translatable("multiplayer.disconnect.not_whitelisted").text()));
                            player.networkHandler.connection.disconnect(TextUtil.translatable("multiplayer.disconnect.not_whitelisted").text());
                        }
                        if (hasBan(player)) {
                            Certificate ban = banned.get(EntityUtil.getName(player));
                            if (ban instanceof TemporaryCertificate temporary) {
                                String remaining = temporary.formatRemaining();
                                player.networkHandler.connection.send(new DisconnectS2CPacket(minecraftTextFormat.format(new Dictionary(ban.getLastLanguage()), "multiplayer.disconnect.banned-time-limited", remaining).text()));
                                player.networkHandler.connection.disconnect(minecraftTextFormat.format(new Dictionary(ban.getLastLanguage()), "multiplayer.disconnect.banned-time-limited", remaining).text());
                            } else {
                                player.networkHandler.connection.send(new DisconnectS2CPacket(minecraftTextFormat.format(new Dictionary(ban.getLastLanguage()), "multiplayer.disconnect.banned-indefinite").text()));
                                player.networkHandler.connection.disconnect(minecraftTextFormat.format(new Dictionary(ban.getLastLanguage()), "multiplayer.disconnect.banned-indefinite").text());
                            }
                        }
                    }
                }
            });

            for (ModMdoDataProcessor processor : modmdoConnections) {
                processor.tick(server);
            }
        }, this, "HandlePlayers");
    }

}
