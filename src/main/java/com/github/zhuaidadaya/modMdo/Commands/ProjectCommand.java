package com.github.zhuaidadaya.modMdo.Commands;

import com.github.zhuaidadaya.MCH.Utils.Config.Config;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import org.json.JSONObject;

import static com.github.zhuaidadaya.modMdo.Storage.Variables.*;
import static com.mojang.brigadier.arguments.StringArgumentType.*;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ProjectCommand {
    public void project() {
        initProject();
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
        LOGGER.info("initializing projects");
        Config<Object, Object> projectConf = config.getConfig("projects");
        if(projectConf != null) {
            projects = new JSONObject(projectConf.getValue());
        } else {
            config.set("projects",new JSONObject());
        }
        LOGGER.info("initialized projects");
    }
}