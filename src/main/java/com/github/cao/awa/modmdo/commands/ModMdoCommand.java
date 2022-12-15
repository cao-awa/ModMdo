package com.github.cao.awa.modmdo.commands;

import com.github.cao.awa.modmdo.*;
import com.github.cao.awa.modmdo.commands.suggester.whitelist.*;
import com.github.cao.awa.modmdo.develop.text.*;
import com.github.cao.awa.modmdo.event.register.*;
import com.github.cao.awa.modmdo.lang.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.utils.command.*;
import com.github.cao.awa.modmdo.utils.text.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.receptacle.*;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.context.*;
import com.mojang.brigadier.exceptions.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.server.command.*;
import net.minecraft.server.network.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;
import static net.minecraft.server.command.CommandManager.*;

public class ModMdoCommand extends SimpleCommand {
    public ModMdoCommand register() {
        SharedVariables.commandRegister.register(literal("modmdo").executes(modmdo -> {
            SimpleCommandOperation.sendFeedback(modmdo, formatModMdoDescription(SimpleCommandOperation.getPlayer(modmdo)));
            return 0;
        }).requires(level -> level.hasPermissionLevel(4)).then(literal("here").executes(here -> {
            SimpleCommandOperation.sendFeedback(here, formatConfigReturnMessage("here_command"));
            return 2;
        }).then(literal("enable").executes(enableHere -> {
            SharedVariables.enableHereCommand = true;
            SharedVariables.saveVariables();
            SimpleCommandOperation.sendFeedback(enableHere, formatEnableHere());
            return 1;
        })).then(literal("disable").executes(disableHere -> {
            SharedVariables.enableHereCommand = false;
            SharedVariables.saveVariables();
            SimpleCommandOperation.sendFeedback(disableHere, formatDisableHere());
            return 0;
        }))).then(literal("useModMdoWhitelist").executes(whitelist -> {
            SharedVariables.saveVariables();

            SimpleCommandOperation.sendFeedback(whitelist, formatConfigReturnMessage("modmdo_whitelist"));
            return 2;
        }).then(literal("enable").executes(enableWhitelist -> {
                config.set("modmdo_whitelist", SharedVariables.modmdoWhitelist = true);
            SharedVariables.saveVariables();

            SimpleCommandOperation.sendFeedback(enableWhitelist, formatUseModMdoWhitelist());
            return 1;
        })).then(literal("disable").executes(disableWhitelist -> {
            config.set("modmdo_whitelist", SharedVariables.modmdoWhitelist = false);
            SharedVariables.saveVariables();
            SimpleCommandOperation.sendFeedback(disableWhitelist, formatDisableModMdoWhitelist());
            return 0;
        }))).then(literal("rejectReconnect").executes(rejectReconnect -> {
            SimpleCommandOperation.sendFeedback(rejectReconnect, formatConfigReturnMessage("reject_reconnect"));
            return 2;
        }).then(literal("enable").executes(reject -> {
                SharedVariables.enableRejectReconnect = true;
                SharedVariables.saveVariables();

                SimpleCommandOperation.sendFeedback(reject, TextUtil.translatable("reject_reconnect.true.rule.format"));
            return 1;
        })).then(literal("disable").executes(receive -> {
            SharedVariables.enableRejectReconnect = false;
            SharedVariables.saveVariables();
            SimpleCommandOperation.sendFeedback(receive, formatDisableRejectReconnect());
            return 0;
        }))).then(literal("itemDespawnTicks").executes(getDespawnTicks -> {
            SimpleCommandOperation.sendFeedback(getDespawnTicks, formatItemDespawnTicks());
            return 2;
        }).then(literal("become").then(argument("ticks", IntegerArgumentType.integer(- 1)).executes(setTicks -> {
            SharedVariables.itemDespawnAge = IntegerArgumentType.getInteger(setTicks, "ticks");

            SimpleCommandOperation.sendFeedbackAndInform(setTicks, formatItemDespawnTicks());
            return 1;
        }))).then(literal("original").executes(setTicksToDefault -> {
            SharedVariables.itemDespawnAge = 6000;

            SimpleCommandOperation.sendFeedbackAndInform(setTicksToDefault, formatItemDespawnTicks());
            return 2;
        }))).then(literal("loginCheckTimeLimit").executes(getTimeLimit -> {
            SimpleCommandOperation.sendFeedback(getTimeLimit, formatCheckerTimeLimit());
            return 0;
        }).then(argument("ms", IntegerArgumentType.integer(500)).executes(setTimeLimit -> {
            config.set("checker_time_limit", IntegerArgumentType.getInteger(setTimeLimit, "ms"));

            SharedVariables.saveVariables();

            SimpleCommandOperation.sendFeedback(setTimeLimit, formatCheckerTimeLimit());
            return 0;
        }))).then(literal("language").executes(getLanguage -> {
            SimpleCommandOperation.sendFeedback(getLanguage, TextUtil.translatable("language.default", SharedVariables.getLanguage()));
            return 0;
        }).then(literal("zh_cn").executes(chinese -> {
            config.set("default_language", com.github.cao.awa.modmdo.lang.Language.ZH_CN);
            SharedVariables.saveVariables();
            SimpleCommandOperation.sendFeedback(chinese, TextUtil.translatable("language.default", SharedVariables.getLanguage()));
            return 0;
        })).then(literal("en_us").executes(english -> {
            config.set("default_language", Language.EN_US);
            SharedVariables.saveVariables();
            SimpleCommandOperation.sendFeedback(english, TextUtil.translatable("language.default", SharedVariables.getLanguage()));
            return 0;
        }))).then(literal("onlyCheckIdentifier").executes(check -> {
            SimpleCommandOperation.sendFeedback(check, formatConfigReturnMessage("whitelist_only_id"));
            return 0;
        }).then(literal("enable").executes(enable -> {
            config.set("whitelist_only_id", true);
            SimpleCommandOperation.sendFeedback(enable, formatConfigReturnMessage("whitelist_only_id"));
            return 0;
        })).then(literal("disable").executes(disable -> {
            config.set("whitelist_only_id", false);
            SimpleCommandOperation.sendFeedback(disable, formatConfigReturnMessage("whitelist_only_id"));
            return 0;
        }))).then(literal("whitelist").then(literal("remove").then(argument("name", StringArgumentType.string()).suggests(ModMdoWhitelistSuggester::suggestions).executes(remove -> {
                    String name = StringArgumentType.getString(remove, "name");
                    if (SharedVariables.whitelistsService.containsName(name)) {
                        SharedVariables.whitelistsService.delete(name);
                        SimpleCommandOperation.sendFeedback(remove, TextUtil.translatable("modmdo.whitelist.removed", name));
                        SharedVariables.saveVariables();
                        return 0;
                    }
                    SimpleCommandOperation.sendError(remove, TextUtil.translatable("arguments.permanent.whitelist.not.registered"));
                    return - 1;
                }))).then(literal("list").executes(showWhiteList -> {
                    showWhitelist(showWhiteList);
                    return 0;
                }))
        ).then(literal("compatibleOnlineMode").executes(getCompatible -> {
            SimpleCommandOperation.sendFeedback(getCompatible, formatConfigReturnMessage("compatible_online_mode"));
            return 0;
        }).then(literal("enable").executes(enable -> {
            config.set("compatible_online_mode", true);
            SimpleCommandOperation.sendFeedback(enable, formatConfigReturnMessage("compatible_online_mode"));
            return 0;
        })).then(literal("disable").executes(disable -> {
            config.set("compatible_online_mode", false);
            SimpleCommandOperation.sendFeedback(disable, formatConfigReturnMessage("compatible_online_mode"));
            return 0;
        }))).then(literal("modmdoConnecting").executes(modmdoConnecting -> {
            SimpleCommandOperation.sendFeedback(modmdoConnecting, formatConfigReturnMessage("modmdo_connecting"));
            return 0;
        }).then(literal("enable").executes(enable -> {
            config.set("modmdo_connecting", true);
            SimpleCommandOperation.sendFeedback(enable, formatConfigReturnMessage("modmdo_connecting"));
            return 0;
        })).then(literal("disable").executes(disable -> {
            config.set("modmdo_connecting", false);
            SimpleCommandOperation.sendFeedback(disable, formatConfigReturnMessage("modmdo_connecting"));
            return 0;
        }))).then(literal("event").then(literal("list").executes(list -> {
            StringBuilder builder = new StringBuilder();
            event.events.forEach((k, v) -> {
                if (v.registered() > 0) {
                    ObjectArrayList<ModMdoEventRegister> registered = v.getRegistered();
                    builder.append("§7").append(v.getName()).append(": ").append("\n");
                    for (ModMdoEventRegister register : registered) {
                        builder.append(register.getExtra().getId().equals(EXTRA_ID) ? "§b" : "§6").append(" ").append(register.getName()).append("\n");
                    }
                }
            });
            builder.append(textFormatService.format(loginUsers.getUser(getPlayer(list)), "modmdo.event.total", event.registered()).getString());
            sendFeedback(list, TextUtil.translatable(builder.toString()));
            return 0;
        })).then(literal("reload").executes(e -> {
            Legacy<Integer, Integer> legacy = ModMdoStdInitializer.loadEvent(true);
            sendFeedback(e, TextUtil.translatable("modmdo.event.reload.success", legacy.newly(), legacy.stale()));
            return 0;
        }))));
        return this;
    }

    public void showWhitelist(CommandContext<ServerCommandSource> source) throws CommandSyntaxException {
        SharedVariables.handleTemporaryWhitelist();
        ServerPlayerEntity player = SimpleCommandOperation.getPlayer(source);
        if (SharedVariables.whitelistsService.count() > 0) {
            StringBuilder builder = new StringBuilder();
            SharedVariables.whitelistsService.forEach(wl -> builder.append(wl.getName()).append(", "));
            builder.delete(builder.length() - 2, builder.length());
            SimpleCommandOperation.sendMessage(player, TextUtil.translatable("commands.modmdo.whitelist.list", SharedVariables.whitelistsService.count(), builder.toString()), false);
        } else {
            SimpleCommandOperation.sendMessage(player, TextUtil.translatable("commands.modmdo.whitelist.none"), false);

        }
    }

    public Translatable formatConfigReturnMessage(String config) {
        return TextUtil.translatable(config + "." + SharedVariables.config.getString(config) + ".rule.format");
    }

    public Translatable formatCheckerTimeLimit() {
        return TextUtil.translatable("checker_time_limit.rule.format", config.getInt("checker_time_limit"));
    }

    public Translatable formatItemDespawnTicks() {
        if (SharedVariables.itemDespawnAge > - 1) {
            return TextUtil.translatable("item.despawn.ticks.rule.format", SharedVariables.itemDespawnAge);
        } else {
            return TextUtil.translatable("item.despawn.ticks.false.rule.format", SharedVariables.itemDespawnAge);
        }
    }

    public Translatable formatEnableHere() {
        return TextUtil.translatable("here_command.true.rule.format");
    }

    public Translatable formatDisableHere() {
        return TextUtil.translatable("here_command.false.rule.format");
    }

    public Translatable formatUseModMdoWhitelist() {
        return TextUtil.translatable("modmdo_whitelist.true.rule.format");
    }

    public Translatable formatDisableModMdoWhitelist() {
        return TextUtil.translatable("modmdo_whitelist.false.rule.format");
    }

    public Translatable formatDisableRejectReconnect() {
        return TextUtil.translatable("reject_reconnect.reject.false.rule.format");
    }

    public Translatable formatModMdoDescription(ServerPlayerEntity player) {
        Translatable modmdoVersion;
        String name;
        if ((name = getPlayerModMdoName(player)) != null) {
            modmdoVersion = TextUtil.translatable("modmdo.description.your.modmdo", name);
        } else {
            modmdoVersion = TextUtil.translatable("");
        }
        return TextUtil.translatable("modmdo.description", SharedVariables.MODMDO_VERSION_NAME, SharedVariables.RELEASE_TIME, modmdoVersion);
    }
}
