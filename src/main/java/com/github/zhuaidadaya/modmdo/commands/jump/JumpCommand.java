package com.github.zhuaidadaya.modmdo.commands.jump;

import com.github.zhuaidadaya.modmdo.commands.ConfigurableCommand;
import com.github.zhuaidadaya.modmdo.commands.SimpleCommandOperation;
import com.github.zhuaidadaya.modmdo.commands.init.ArgumentInit;
import com.github.zhuaidadaya.modmdo.jump.server.ServerInformation;
import com.github.zhuaidadaya.modmdo.jump.server.ServerUtil;
import com.github.zhuaidadaya.modmdo.login.token.ServerEncryptionToken;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import static com.github.zhuaidadaya.modmdo.storage.Variables.*;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class JumpCommand extends SimpleCommandOperation implements ConfigurableCommand {
    @Override
    public void register() {
        init();

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("jump").then(literal("remove").requires(level -> level.hasPermissionLevel(4)).then(argument("servers", ServerJumpArgument.servers()).executes(removeServer -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_JUMP, getPlayer(removeServer), this, removeServer)) {
                    ServerPlayerEntity player = getPlayer(removeServer);
                    ServerInformation information = ServerJumpArgument.getServer(removeServer, "servers");
                    try {
                        if(information.isError())
                            throw new IllegalStateException("server information is error");
                        servers.remove(information.getName());
                        updateServersJump();
                        ArgumentInit.initServerJump();
                        servers.updateToPlayer(player.networkHandler.connection);

                        sendFeedback(removeServer, formatRemoveServerSuccess(information.getName()));
                    } catch (Exception e) {
                        sendFeedback(removeServer, formatRemoveServerFailed(information.getName()));
                    }
                }
                return 0;
            }))).then(literal("add").requires(level -> level.hasPermissionLevel(4)).then(argument("name", StringArgumentType.string()).then(argument("host", StringArgumentType.string()).then(argument("port", IntegerArgumentType.integer(1, 65565)).executes(addServer -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_JUMP, getPlayer(addServer), this, addServer)) {
                    ServerPlayerEntity player = getPlayer(addServer);
                    String name = StringArgumentType.getString(addServer, "name");
                    String host = StringArgumentType.getString(addServer, "host");
                    int port = IntegerArgumentType.getInteger(addServer, "port");
                    try {
                        servers.add(host, port, name);
                        updateServersJump();
                        ArgumentInit.initServerJump();
                        servers.updateToPlayer(player.networkHandler.connection);

                        sendFeedback(addServer, formatAddSuccess(name));
                    } catch (Exception e) {
                        sendFeedback(addServer, formatAddFailed(name));
                    }
                }
                return 0;
            }))))).then(literal("to").then(argument("servers", ServerJumpArgument.servers()).executes(jump -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_JUMP, getPlayer(jump), this, jump)) {
                    ServerInformation jumpTo = ServerJumpArgument.getServer(jump, "servers");
                    try {
                        ServerPlayerEntity player = getPlayer(jump);

                        player.networkHandler.sendPacket(new CustomPayloadS2CPacket(modMdoServerChannel, new PacketByteBuf(Unpooled.buffer()).writeVarInt(106).writeString(jumpTo.getName())));

                        forceStopTokenCheck = true;

                        player.networkHandler.disconnect(new LiteralText("jump server"));
                    } catch (Exception e) {
                        sendFeedback(jump, formatJumpFailed(jumpTo.getName()));
                    }
                }
                return 0;
            }).then(literal("default").executes(jumpAsDefault -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_JUMP, getPlayer(jumpAsDefault), this, jumpAsDefault)) {
                    ServerInformation jumpTo = ServerJumpArgument.getServer(jumpAsDefault, "servers");
                    try {
                        ServerPlayerEntity player = getPlayer(jumpAsDefault);

                        if(jumpTo.getServerToken().getServerDefaultToken().equals(""))
                            player.networkHandler.sendPacket(new CustomPayloadS2CPacket(modMdoServerChannel, new PacketByteBuf(Unpooled.buffer()).writeVarInt(106).writeString(jumpTo.getName())));
                        else
                            player.networkHandler.sendPacket(new CustomPayloadS2CPacket(modMdoServerChannel, new PacketByteBuf(Unpooled.buffer()).writeVarInt(105).writeString(jumpTo.getName()).writeString(jumpTo.getServerToken().getServerDefaultToken()).writeString("default")));

                        forceStopTokenCheck = true;

                        player.networkHandler.disconnect(new LiteralText("jump server"));
                    } catch (Exception e) {
                        sendFeedback(jumpAsDefault, formatJumpFailed(jumpTo.getName()));
                    }
                }
                return 0;
            })).then(literal("ops").requires(level -> level.hasPermissionLevel(4)).executes(jumpAsOps -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_JUMP, getPlayer(jumpAsOps), this, jumpAsOps)) {
                    ServerInformation jumpTo = ServerJumpArgument.getServer(jumpAsOps, "servers");
                    try {
                        ServerPlayerEntity player = getPlayer(jumpAsOps);
                        if(loginUsers.getUser(player).getLevel() == 4) {

                            if(jumpTo.getServerToken().getServerOpsToken().equals(""))
                                player.networkHandler.sendPacket(new CustomPayloadS2CPacket(modMdoServerChannel, new PacketByteBuf(Unpooled.buffer()).writeVarInt(106).writeString(jumpTo.getName())));
                            else
                                player.networkHandler.sendPacket(new CustomPayloadS2CPacket(modMdoServerChannel, new PacketByteBuf(Unpooled.buffer()).writeVarInt(105).writeString(jumpTo.getName()).writeString(jumpTo.getServerToken().getServerOpsToken()).writeString("ops")));

                            forceStopTokenCheck = true;

                            player.networkHandler.disconnect(new LiteralText("jump server"));
                        } else {
                            throw new Exception();
                        }
                    } catch (Exception e) {
                        sendFeedback(jumpAsOps, formatJumpFailed(jumpTo.getName()));
                    }
                }
                return 0;
            })))).then(literal("token").requires(level -> level.hasPermissionLevel(4)).then(literal("set").then(argument("server", ServerJumpArgument.servers()).then(literal("default").then(literal("from").then(literal("this").executes(setDefaultToken -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_JUMP, getPlayer(setDefaultToken), this, setDefaultToken)) {
                    try {
                        ServerInformation server = ServerJumpArgument.getServer(setDefaultToken, "server");

                        if(server.isError())
                            throw new Exception();

                        ServerEncryptionToken token = server.getServerToken();
                        token.setServerDefaultToken(modMdoToken.getServerToken().getServerDefaultToken());
                        server.setToken(token);

                        servers.set(server);

                        updateServersJump();

                        sendFeedback(setDefaultToken, formatSetTokenSuccess(server.getName(), "default"));
                    } catch (Exception e) {
                        sendFeedback(setDefaultToken, formatSetTokenFailed(server.getName()));
                    }
                }
                return 0;
            })).then(literal("sourceFile").then(argument("file", StringArgumentType.string()).executes(setDefaultFromSource -> {
                ServerInformation server = ServerJumpArgument.getServer(setDefaultFromSource, "server");
                if(commandApplyToPlayer(MODMDO_COMMAND_JUMP, getPlayer(setDefaultFromSource), this, setDefaultFromSource)) {
                    try {
                        File f = new File(StringArgumentType.getString(setDefaultFromSource, "file"));

                        BufferedReader br = new BufferedReader(new FileReader(f));

                        String cache;
                        StringBuilder token = new StringBuilder();
                        while((cache = br.readLine()) != null) {
                            token.append(cache);
                        }

                        br.close();

                        if(server.isError())
                            throw new Exception();

                        ServerEncryptionToken serverToken = server.getServerToken();
                        serverToken.setServerDefaultToken(token.toString());
                        server.setToken(serverToken);

                        servers.set(server);

                        updateServersJump();

                        sendFeedback(setDefaultFromSource, formatSetTokenSuccess(server.getName(), "default"));
                    } catch (Exception e) {
                        sendFeedback(setDefaultFromSource, formatSetTokenFailed(server.getName()));
                    }
                }
                return 0;
            }))).then(literal("jsonFile").then(argument("file", StringArgumentType.string()).executes(setDefaultFromJson -> {
                ServerInformation server = ServerJumpArgument.getServer(setDefaultFromJson, "server");
                if(commandApplyToPlayer(MODMDO_COMMAND_JUMP, getPlayer(setDefaultFromJson), this, setDefaultFromJson)) {
                    try {
                        File f = new File(StringArgumentType.getString(setDefaultFromJson, "file"));

                        BufferedReader br = new BufferedReader(new FileReader(f));

                        String cache;
                        StringBuilder token = new StringBuilder();
                        while((cache = br.readLine()) != null) {
                            token.append(cache);
                        }

                        br.close();

                        if(server.isError())
                            throw new Exception();

                        JSONObject json = new JSONObject(token.toString());

                        ServerEncryptionToken serverToken = server.getServerToken();
                        serverToken.setServerDefaultToken(json.getJSONObject("server").getString("default"));
                        server.setToken(serverToken);

                        servers.set(server);

                        updateServersJump();

                        sendFeedback(setDefaultFromJson, formatSetTokenSuccess(server.getName(), "default"));
                    } catch (Exception e) {
                        sendFeedback(setDefaultFromJson, formatSetTokenFailed(server.getName()));
                    }
                }
                return 0;
            }))))).then(literal("ops").then(literal("from").then(literal("this").executes(setOpsFromThis -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_JUMP, getPlayer(setOpsFromThis), this, setOpsFromThis)) {
                    try {
                        ServerInformation server = ServerJumpArgument.getServer(setOpsFromThis, "server");

                        if(server.isError())
                            throw new Exception();

                        ServerEncryptionToken token = server.getServerToken();
                        token.setServerOpsToken(modMdoToken.getServerToken().getServerOpsToken());
                        server.setToken(token);

                        servers.set(server);

                        updateServersJump();

                        sendFeedback(setOpsFromThis, formatSetTokenSuccess(server.getName(), "ops"));
                    } catch (Exception e) {
                        sendFeedback(setOpsFromThis, formatSetTokenFailed(server.getName()));
                    }
                }
                return 0;
            })).then(literal("sourceFile").then(argument("file", StringArgumentType.string()).executes(setOpsFromSource -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_JUMP, getPlayer(setOpsFromSource), this, setOpsFromSource)) {
                    ServerInformation server = ServerJumpArgument.getServer(setOpsFromSource, "server");
                    try {
                        File f = new File(StringArgumentType.getString(setOpsFromSource, "file"));

                        BufferedReader br = new BufferedReader(new FileReader(f));

                        String cache;
                        StringBuilder token = new StringBuilder();
                        while((cache = br.readLine()) != null) {
                            token.append(cache);
                        }

                        br.close();

                        if(server.isError())
                            throw new Exception();

                        ServerEncryptionToken serverToken = server.getServerToken();
                        serverToken.setServerOpsToken(token.toString());
                        server.setToken(serverToken);

                        servers.set(server);

                        updateServersJump();

                        sendFeedback(setOpsFromSource, formatSetTokenSuccess(server.getName(), "ops"));
                    } catch (Exception e) {
                        sendFeedback(setOpsFromSource, formatSetTokenFailed(server.getName()));
                    }
                }
                return 0;
            }))).then(literal("jsonFile").then(argument("file", StringArgumentType.string()).executes(setOpsFromJson -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_JUMP, getPlayer(setOpsFromJson), this, setOpsFromJson)) {
                    ServerInformation server = ServerJumpArgument.getServer(setOpsFromJson, "server");
                    try {
                        File f = new File(StringArgumentType.getString(setOpsFromJson, "file"));

                        BufferedReader br = new BufferedReader(new FileReader(f));

                        String cache;
                        StringBuilder token = new StringBuilder();
                        while((cache = br.readLine()) != null) {
                            token.append(cache);
                        }

                        br.close();

                        if(server.isError())
                            throw new Exception();

                        JSONObject json = new JSONObject(token.toString());

                        ServerEncryptionToken serverToken = server.getServerToken();
                        serverToken.setServerOpsToken(json.getJSONObject("server").getString("ops"));
                        server.setToken(serverToken);

                        servers.set(server);

                        updateServersJump();

                        sendFeedback(setOpsFromJson, formatSetTokenSuccess(server.getName(), "ops"));
                    } catch (Exception e) {
                        sendFeedback(setOpsFromJson, formatSetTokenFailed(server.getName()));
                    }
                }
                return 0;
            }))))))).then(literal("remove").then(argument("server", ServerJumpArgument.servers()).executes(removeAllToken -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_JUMP, getPlayer(removeAllToken), this, removeAllToken)) {
                    ServerInformation server = ServerJumpArgument.getServer(removeAllToken, "server");
                    try {
                        if(server.isError())
                            throw new Exception();

                        boolean hasDefault;
                        boolean hasOps;

                        String formatRemoved;

                        hasDefault = ! server.getServerToken().getServerDefaultToken().equals("");
                        hasOps = ! server.getServerToken().getServerOpsToken().equals("");

                        if(! hasDefault & ! hasOps) {
                            throw new Exception();
                        } else {
                            if(hasDefault & hasOps)
                                formatRemoved = "default/ops";
                            else
                                formatRemoved = hasDefault ? "default" : "ops";
                        }

                        ServerEncryptionToken token = server.getServerToken();
                        token.setServerDefaultToken("");
                        token.setServerOpsToken("");
                        server.setToken(token);

                        sendFeedback(removeAllToken, formatRemoveTokenSuccess(server.getName(), formatRemoved));
                    } catch (Exception e) {
                        sendFeedback(removeAllToken, formatRemoveTokenFailed(server.getName()));
                    }
                }
                return 0;
            }).then(literal("default").executes(removeDefault -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_JUMP, getPlayer(removeDefault), this, removeDefault)) {
                    ServerInformation server = ServerJumpArgument.getServer(removeDefault, "server");
                    try {
                        if(server.isError())
                            throw new Exception();

                        if(server.getServerToken().getServerDefaultToken().equals("")) {
                            throw new Exception();
                        }

                        ServerEncryptionToken token = server.getServerToken();
                        token.setServerDefaultToken("");
                        server.setToken(token);

                        sendFeedback(removeDefault, formatRemoveTokenSuccess(server.getName(), "default"));
                    } catch (Exception e) {
                        sendFeedback(removeDefault, formatRemoveTokenFailed(server.getName()));
                    }
                }
                return 0;
            })).then(literal("ops").executes(removeOps -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_JUMP, getPlayer(removeOps), this, removeOps)) {
                    ServerInformation server = ServerJumpArgument.getServer(removeOps, "server");
                    try {
                        if(server.isError())
                            throw new Exception();

                        if(server.getServerToken().getServerOpsToken().equals("")) {
                            throw new Exception();
                        }

                        ServerEncryptionToken token = server.getServerToken();
                        token.setServerOpsToken("");
                        server.setToken(token);

                        sendFeedback(removeOps, formatRemoveTokenSuccess(server.getName(), "ops"));
                    } catch (Exception e) {
                        sendFeedback(removeOps, formatRemoveTokenFailed(server.getName()));
                    }
                }
                return 0;
            }))))));
        });
    }

    public TranslatableText formatRemoveServerSuccess(String name) {
        return new TranslatableText("server.remove.sus", name);
    }

    public TranslatableText formatRemoveServerFailed(String name) {
        return new TranslatableText("server.remove.failed", name);
    }

    public TranslatableText formatAddSuccess(String name) {
        return new TranslatableText("server.add.sus", name);
    }

    public TranslatableText formatAddFailed(String name) {
        return new TranslatableText("server.add.failed", name);
    }

    public TranslatableText formatSetTokenSuccess(String name, String type) {
        return new TranslatableText("server.set.token.sus", name, type);
    }

    public TranslatableText formatSetTokenFailed(String name) {
        return new TranslatableText("server.set.token.failed", name);
    }

    public TranslatableText formatRemoveTokenSuccess(String name, String type) {
        return new TranslatableText("server.remove.token.sus", name, type);
    }

    public TranslatableText formatRemoveTokenFailed(String name) {
        return new TranslatableText("server.remove.token.failed", name);
    }

    public TranslatableText formatJumpFailed(String name) {
        return new TranslatableText("server.jump.failed", name);
    }

    @Override
    public void init() {
        LOGGER.info("initializing servers jump");

        Object serversJump = config.getConfig("servers_jump");
        if(serversJump != null) {
            servers = new ServerUtil(new JSONObject(serversJump.toString()));
        } else {
            servers = new ServerUtil();
            config.set("servers_jump", new JSONObject());
        }

        ArgumentInit.initServerJump();

        updateServersJump();

        LOGGER.info("initialized servers jump");
    }
}
