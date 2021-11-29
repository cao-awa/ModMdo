package com.github.zhuaidadaya.modMdo.Commands;

import com.mojang.brigadier.Command;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.network.MessageType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.File;

import static com.mojang.brigadier.arguments.StringArgumentType.*;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ProjectCommand {
    public static int broadcast(ServerCommandSource source, Formatting formatting, String message) {
        try {
            final Text text = new LiteralText(message).formatted(formatting);

            source.getServer().getPlayerManager().broadcastChatMessage(text, MessageType.CHAT, source.getPlayer().getUuid());
            return Command.SINGLE_SUCCESS;
        } catch (Exception e) {
            return - 1;
        }
    }

    public void project() {
        //        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
        //            dispatcher.register(literal("broadcast")
        //                    .requires(source -> source.hasPermissionLevel(2)) // Must be a game master to use the command. Command will not show up in tab completion or execute to non operators or any operator that is permission level 1.
        //                    .then(argument("color", ColorArgumentType.color())
        //                            .then(argument("message", greedyString())
        //                                    .executes(ctx -> broadcast(ctx.getSource(), getColor(ctx, "color"), getString(ctx, "message")))))); // You can deal with the arguments out here and pipe them into the command.
        //
        //
        //        });

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("projects").then(literal("start").then(argument("projectName", string()).executes(ctx -> {
                System.out.println(getString(ctx, "projectName"));
                return 1;
            }).then(argument("projectNote",greedyString()).executes(c-> {
                System.out.println("project note");
                return 2;
            })))).then(literal("test").executes(c -> {
                System.out.println("test");
                return 0;
            })));
        });

    }

    public void initProject() {
        new File("projects/project.mhf");
    }
}