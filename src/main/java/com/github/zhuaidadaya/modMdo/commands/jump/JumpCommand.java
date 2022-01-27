package com.github.zhuaidadaya.modMdo.commands.jump;

import com.github.zhuaidadaya.modMdo.commands.ConfigurableCommand;
import com.github.zhuaidadaya.modMdo.commands.SimpleCommandOperation;
import com.github.zhuaidadaya.modMdo.commands.init.ArgumentInit;
import com.github.zhuaidadaya.modMdo.jump.server.ServerInformation;
import com.github.zhuaidadaya.modMdo.jump.server.ServerUtil;
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

import static com.github.zhuaidadaya.modMdo.storage.Variables.*;
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
                        player.networkHandler.sendPacket(new CustomPayloadS2CPacket(modMdoServerChannel, new PacketByteBuf(Unpooled.buffer()).writeVarInt(107).writeString(servers.toJSONObject().toString())));

                        sendFeedback(removeServer, formatRemoveSuccess(information.getName()));
                    } catch (Exception e) {
                        sendFeedback(removeServer, formatRemoveFailed(information.getName()));
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
                        player.networkHandler.sendPacket(new CustomPayloadS2CPacket(modMdoServerChannel, new PacketByteBuf(Unpooled.buffer()).writeVarInt(107).writeString(servers.toJSONObject().toString())));

                        sendFeedback(addServer, formatAddSuccess(name));
                    } catch (Exception e) {
                        sendFeedback(addServer, formatAddFailed(name));
                    }
                }
                return 0;
            }))))).then(literal("to").then(argument("servers", ServerJumpArgument.servers()).executes(jump -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_JUMP, getPlayer(jump), this, jump)) {
                    try {
                        ServerPlayerEntity player = getPlayer(jump);

                        ServerInformation jumpTo = ServerJumpArgument.getServer(jump, "servers");

                        player.networkHandler.sendPacket(new CustomPayloadS2CPacket(modMdoServerChannel, new PacketByteBuf(Unpooled.buffer()).writeVarInt(106).writeString(jumpTo.getName())));

                        forceStopTokenCheck = true;

                        player.networkHandler.disconnect(new LiteralText("jump server"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return 0;
            }))));
        });
    }

    public TranslatableText formatRemoveSuccess(String name) {
        return new TranslatableText("server.remove.sus", name);
    }

    public TranslatableText formatRemoveFailed(String name) {
        return new TranslatableText("server.remove.failed", name);
    }

    public TranslatableText formatAddSuccess(String name) {
        return new TranslatableText("server.add.sus", name);
    }

    public TranslatableText formatAddFailed(String name) {
        return new TranslatableText("server.add.failed", name);
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
