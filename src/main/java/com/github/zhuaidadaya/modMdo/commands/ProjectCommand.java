package com.github.zhuaidadaya.modMdo.commands;

import com.github.zhuaidadaya.MCH.utils.config.Config;
import com.github.zhuaidadaya.modMdo.projects.Project;
import com.github.zhuaidadaya.modMdo.projects.ProjectUtil;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.json.JSONObject;

import static com.github.zhuaidadaya.modMdo.storage.Variables.*;
import static com.mojang.brigadier.arguments.StringArgumentType.*;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

@Deprecated
public class ProjectCommand {

    @Deprecated
    public void register() {
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
            dispatcher.register(literal("projects").then(literal("start").then(argument("projectName", string()).executes(start -> {
                ServerPlayerEntity player = start.getSource().getPlayer();
                Project project = new Project(getString(start, "projectName"), users.getUser(player));
                startProject(project);

                new ArgumentInit().init();

                return 1;
            }).then(argument("projectNote", greedyString()).executes(c -> {
                System.out.println("project note");
                return 2;
            })))).then(literal("detailed").then(argument("projects", ProjectListArgument.projectList()).executes(projectsInfo -> {
                String[] input = projectsInfo.getInput().split(" ");
                System.out.println(input[2]);
                return 3;
            }).then(literal("initiator").executes(initiator -> {
                String[] input = initiator.getInput().split(" ");
                System.out.println(input[2]);
                initiator.getSource().sendFeedback(Text.of(initiator.getLastChild().getInput()), true);
                return 0;
            })))));
        });
    }

    @Deprecated
    public void startProject(Project project) {
        projects.addProject(project);

        updateUserProfiles();
        updateProjects();
    }

    @Deprecated
    public void initProject() {
        LOGGER.info("initializing projects");
        Config<Object, Object> projectConf = config.getConfig("projects");
        if(projectConf != null) {
            projects = new ProjectUtil(new JSONObject(projectConf.getValue()));
        } else {
            projects = new ProjectUtil();
            config.set("projects", new JSONObject());
        }
        LOGGER.info("initialized projects");

        updateProjects();
    }
}