package com.github.zhuaidadaya.modMdo.commands.wrap;

import com.github.zhuaidadaya.modMdo.commands.ConfigurableCommand;
import com.github.zhuaidadaya.modMdo.commands.SimpleCommandOperation;
import com.github.zhuaidadaya.modMdo.commands.init.ArgumentInit;
import com.github.zhuaidadaya.modMdo.wrap.server.ServerInformation;
import com.github.zhuaidadaya.modMdo.wrap.server.ServerUtil;
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

public class WrapCommand extends SimpleCommandOperation implements ConfigurableCommand {
    @Override
    public void register() {
        init();

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("wrap").then(literal("remove").requires(level -> level.hasPermissionLevel(4)).then(argument("servers", ServerWrapArgument.servers()).executes(removeServer -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_WRAP, getPlayer(removeServer), this, removeServer)) {
                    ServerPlayerEntity player = getPlayer(removeServer);
                    ServerInformation information = ServerWrapArgument.getServer(removeServer, "servers");
                    try {
                        if(information.isError())
                            throw new IllegalStateException("server information is error");
                        servers.remove(information.getName());
                        updateServersWrap();
                        ArgumentInit.initServerWrap();
                        player.networkHandler.sendPacket(new CustomPayloadS2CPacket(modMdoServerChannel, new PacketByteBuf(Unpooled.buffer()).writeVarInt(107).writeString(servers.toJSONObject().toString())));

                        sendFeedback(removeServer, formatRemoveSuccess(information.getName()));
                    } catch (Exception e) {
                        sendFeedback(removeServer, formatRemoveFailed(information.getName()));
                    }
                }
                return 0;
            }))).then(literal("add").requires(level -> level.hasPermissionLevel(4)).then(argument("name", StringArgumentType.string()).then(argument("host", StringArgumentType.string()).then(argument("port", IntegerArgumentType.integer(1, 65565)).executes(addServer -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_WRAP, getPlayer(addServer), this, addServer)) {
                    ServerPlayerEntity player = getPlayer(addServer);
                    String name = StringArgumentType.getString(addServer, "name");
                    String host = StringArgumentType.getString(addServer, "host");
                    int port = IntegerArgumentType.getInteger(addServer, "port");
                    try {
                        servers.add(host, port, name);
                        updateServersWrap();
                        ArgumentInit.initServerWrap();
                        player.networkHandler.sendPacket(new CustomPayloadS2CPacket(modMdoServerChannel, new PacketByteBuf(Unpooled.buffer()).writeVarInt(107).writeString(servers.toJSONObject().toString())));

                        sendFeedback(addServer, formatAddSuccess(name));
                    } catch (Exception e) {
                        sendFeedback(addServer, formatAddFailed(name));
                    }
                }
                return 0;
            }))))).then(literal("to").then(argument("servers", ServerWrapArgument.servers()).executes(wrap -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_WRAP, getPlayer(wrap), this, wrap)) {
                    try {
                        ServerPlayerEntity player = getPlayer(wrap);

                        ServerInformation wrapTo = ServerWrapArgument.getServer(wrap, "servers");

                        player.networkHandler.sendPacket(new CustomPayloadS2CPacket(modMdoServerChannel, new PacketByteBuf(Unpooled.buffer()).writeVarInt(106).writeString(wrapTo.getName())));

                        forceStopTokenCheck = true;

                        player.networkHandler.disconnect(new LiteralText("wrap server"));
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
        LOGGER.info("initializing servers wrap");

        Object serversWrap = config.getConfig("servers_wrap");
        if(serversWrap != null) {
            servers = new ServerUtil(new JSONObject(serversWrap.toString()));
        } else {
            servers = new ServerUtil();
            config.set("servers_wrap", new JSONObject());
        }

        ArgumentInit.initServerWrap();

        updateServersWrap();

        LOGGER.info("initialized servers wrap");
    }
}
