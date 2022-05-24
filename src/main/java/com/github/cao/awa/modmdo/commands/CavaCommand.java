package com.github.cao.awa.modmdo.commands;

import com.github.cao.awa.modmdo.cavas.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.utils.command.*;
import com.github.cao.awa.modmdo.utils.usr.*;
import com.mojang.brigadier.arguments.*;
import net.minecraft.server.command.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;
import org.json.*;

import static net.minecraft.server.command.CommandManager.*;

public class CavaCommand extends ConfigurableCommand<CavaCommand> {
    public CavaCommand init() {
        Object projectConf = SharedVariables.config.getConfig("cavas");
        if (projectConf != null) {
            SharedVariables.cavas = new CavaUtil(new JSONObject(projectConf.toString()));
        } else {
            SharedVariables.cavas = new CavaUtil();
            SharedVariables.config.set("cavas", new JSONObject());
        }

        SharedVariables.updateCavas();
        return this;
    }

    public CavaCommand register() {
        SharedVariables.commandRegister.register(literal("cava").executes(cava -> {
            ServerCommandSource source = cava.getSource();

            if (SharedVariables.enableCava) {
                try {
                    SimpleCommandOperation.sendFeedback(source, formatCavaTip(SimpleCommandOperation.getPlayer(cava)), 1);
                } catch (Exception e) {
                    SimpleCommandOperation.sendError(source, formatNoCava(), 1);
                }
            } else {
                SimpleCommandOperation.sendError(source, formatCavaDisabled(), 1);
            }
            return 0;
        }).then(literal("create").then(argument("cava_message", StringArgumentType.greedyString()).executes(createCava -> {
            ServerCommandSource source = createCava.getSource();
            ServerPlayerEntity player = SimpleCommandOperation.getPlayer(createCava);
            if (SharedVariables.enableCava) {
                String cavaMessage = createCava.getInput();

                try {
                    Cava cava = SharedVariables.cavas.createCava(SharedVariables.users.getUser(player), cavaMessage.substring(13));

                    SharedVariables.LOGGER.info(String.format("player %s(%s) created a Cava, Cava id: %s", player.getName().asString(), player.getUuid(), cava.getID()));

                    SimpleCommandOperation.sendFeedback(source, formatCavaCreated(cava.getID()), 1);
                } catch (IllegalArgumentException e) {
                    SimpleCommandOperation.sendError(source, formatCavaExists(), 1);
                } catch (Exception e) {
                    SimpleCommandOperation.sendError(source, formatCavaCreateFail(), 1);
                }
            } else {
                SimpleCommandOperation.sendError(source, formatCavaDisabled(), 1);
            }
            return 1;
        }))).then(literal("deleteLast").requires(level -> level.hasPermissionLevel(4)).executes(deleteCava -> {
            ServerCommandSource source = deleteCava.getSource();
            ServerPlayerEntity player = source.getPlayer();

            if (SharedVariables.enableCava) {
                try {
                    String cavaID = SharedVariables.users.getUserConfig(player.getUuid().toString(), "lastCava").toString();

                    SharedVariables.cavas.deleteCava(cavaID);

                    SharedVariables.LOGGER.info(String.format("player %s(%s) deleted a Cava, Cava id: %s", player.getName().asString(), player.getUuid(), cavaID));

                    SimpleCommandOperation.sendFeedback(source, formatCavaDeleted(cavaID), 1);
                } catch (Exception e) {
                    SimpleCommandOperation.sendError(source, formatCavaDeleteFail(), 1);
                }
            } else {
                SimpleCommandOperation.sendError(source, formatCavaDisabled(), 1);
            }
            return 2;
        })));
        return this;
    }

    public TranslatableText formatCavaDeleteFail() {
        return new TranslatableText("cava.delete.failed");
    }

    public TranslatableText formatCavaDeleted(String cavaID) {
        return new TranslatableText("cava.feedback.deleted", cavaID);
    }

    public TranslatableText formatCavaCreateFail() {
        return new TranslatableText("cava.create.failed");
    }

    public TranslatableText formatCavaExists() {
        return new TranslatableText("cava.create.failed.alreadyExists");
    }

    public TranslatableText formatCavaCreated(String cavaID) {
        return new TranslatableText("cava.feedback.created", cavaID);
    }

    public TranslatableText formatNoCava() {
        return new TranslatableText("command.cava.noCava");
    }

    public TranslatableText formatCavaTip(ServerPlayerEntity player) {
        return new TranslatableText("cava.format", getCava(SharedVariables.users.getUser(player)).getMessage(), player.getName().asString());
    }

    public Cava getCava(User user) {
        Cava cava = SharedVariables.cavas.get();
        SharedVariables.setUserProfile(user, "lastCava", cava.getID());

        SharedVariables.updateUserProfiles();
        return cava;
    }

    public TranslatableText formatCavaDisabled() {
        return new TranslatableText("cava.disable");
    }
}
