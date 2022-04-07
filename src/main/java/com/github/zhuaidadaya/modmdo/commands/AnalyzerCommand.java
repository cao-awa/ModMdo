package com.github.zhuaidadaya.modmdo.commands;

import com.github.zhuaidadaya.modmdo.subscribable.TickPerSecondAnalyzer;
import com.github.zhuaidadaya.modmdo.system.SystemUtil;
import com.github.zhuaidadaya.modmdo.usr.User;
import com.github.zhuaidadaya.modmdo.utils.command.SimpleCommandOperation;
import com.github.zhuaidadaya.modmdo.utils.player.PlayerUtil;
import com.github.zhuaidadaya.modmdo.utils.times.TimeUtil;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

import static com.github.zhuaidadaya.modmdo.storage.Variables.*;
import static com.github.zhuaidadaya.modmdo.storage.Variables.dimensionUtil;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class AnalyzerCommand extends SimpleCommandOperation implements SimpleCommand {
    private final TickPerSecondAnalyzer tps = new TickPerSecondAnalyzer();

    @Override
    public void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("analyzer").then(literal("self").then(literal("vec").executes(vec -> {
                if (commandApplyToPlayer(1, getPlayer(vec), this, vec)) {
                    ServerPlayerEntity player = getPlayer(vec);

                    sendFeedback(vec, formatVecMessage(player.getPos(), player.getRotationClient(), dimensionUtil.getDimension(player)));

                }
                return 0;
            })).then(literal("gameOnlineTime").executes(onlineTime -> {
                ServerPlayerEntity player = getPlayer(onlineTime);

                sendFeedback(onlineTime, formatGameOnlineTime(player), 11);
                return 0;
            }))).then(literal("server").then(literal("cpu").executes(cpu -> {
                new Thread(() -> {
                    sendFeedback(cpu, new TranslatableText("server.cpu", SystemUtil.getCpuTotalUsed() + "(Used)", SystemUtil.getCpuWait() + "(Wait)"), 11);
                }).start();
                return 1;
            })).then(literal("memory").executes(memory -> {
                MemoryMXBean memoryMx = ManagementFactory.getMemoryMXBean();
                MemoryUsage memoryUsage = memoryMx.getHeapMemoryUsage();

                float usedMemory = memoryUsage.getUsed() / 1024f / 1024f;
                float totalMemory = memoryUsage.getMax() / 1024f / 1024f;

                String formatMemoryTag = "§" + (totalMemory / usedMemory < 0.9f ? "d" : (totalMemory / usedMemory < 0.7f ? "c" : "a"));

                sendFeedback(memory, new TranslatableText("server.memory", formatMemoryTag + totalMemory, formatMemoryTag + usedMemory), 11);
                return - 1;
            })).then(literal("tps").executes(tps -> {
                analyzeTickPerSecond(tps, 20);
                return 2;
            }).then(literal("while").then(argument("target", IntegerArgumentType.integer(20, 10000)).executes(tps -> {
                analyzeTickPerSecond(tps, IntegerArgumentType.getInteger(tps, "target"));
                return 2;
            }))).then(literal("keep").executes(keep -> {
                analyzeTickPerSecond(keep, - 1);
                return 1;
            })).then(literal("stop").executes(stop -> {
                tps.stop();
                sendFeedback(stop, new TranslatableText("subscribable.tps.stopped"), 20);
                return 3;
            })).then(literal("subscribe").executes(sub -> {
                if (tps.isRunning()) {
                    ServerPlayerEntity player = getPlayer(sub);
                    if (tps.hasSub(player)) {
                        tps.cancelSub(player);
                    } else {
                        tps.addSub(player);
                    }
                } else {
                    sendFeedback(sub, new TranslatableText("subscribable.tps.not.running"), 20);
                }
                return 3;
            })))));
        });
    }

    public TranslatableText formatVecMessage(Vec3d vec3d, Vec2f vec2f, String dimension) {
        return new TranslatableText("command.vec.format", vec3d.x, vec3d.y, vec3d.z, vec2f.x, vec2f.y, dimension);
    }

    public TranslatableText formatOnlineTime(ServerPlayerEntity player) {
        User user = loginUsers.getUser(player);
        long onlineDays = user.processRemainingDays();

        if (onlineDays > 0) {
            return new TranslatableText("player.online.time.days", onlineDays, user.processRemainingHours(), user.processRemainingMinutes(), user.processRemainingSeconds());
        } else {
            long onlineHours = user.processRemainingHours();

            if (onlineHours > 0) {
                return new TranslatableText("player.online.time.hours", onlineHours, user.processRemainingMinutes(), user.processRemainingSeconds());
            } else {
                long onlineMinutes = user.processRemainingMinutes();
                if (onlineMinutes > 0) {
                    return new TranslatableText("player.online.time.minutes", onlineMinutes, user.processRemainingSeconds());
                } else {
                    return new TranslatableText("player.online.time.seconds", user.processRemainingSeconds());
                }
            }
        }
    }

    public void analyzeTickPerSecond(CommandContext<ServerCommandSource> source, long target) throws CommandSyntaxException {
        if (! tps.isRunning()) {
            tps.init(getServer(source), target);
            sendFeedback(source, new TranslatableText("subscribable.tps.started"), 20);
            ServerPlayerEntity player = getPlayer(source);
            tps.addSub(player);
        } else {
            sendFeedback(source, new TranslatableText("subscribable.tps.already.running"), 20);
        }
    }

    public TranslatableText formatGameOnlineTime(ServerPlayerEntity player) {
        long millionTotal = TimeUtil.ticksMillionTotal(PlayerUtil.getPlayTime(player));
        long onlineDays = TimeUtil.processRemainingDays(millionTotal);

        if (onlineDays > 0) {
            return new TranslatableText("player.online.time.days", onlineDays, TimeUtil.processRemainingHours(millionTotal), TimeUtil.processRemainingMinutes(millionTotal), TimeUtil.processRemainingSeconds(millionTotal));
        } else {
            long onlineHours = TimeUtil.processRemainingHours(millionTotal);

            if (onlineHours > 0) {
                return new TranslatableText("player.online.time.hours", onlineHours, TimeUtil.processRemainingMinutes(millionTotal), TimeUtil.processRemainingSeconds(millionTotal));
            } else {
                long onlineMinutes = TimeUtil.processRemainingMinutes(millionTotal);
                if (onlineMinutes > 0) {
                    return new TranslatableText("player.online.time.minutes", onlineMinutes, TimeUtil.processRemainingSeconds(millionTotal));
                } else {
                    return new TranslatableText("player.online.time.seconds", TimeUtil.processRemainingSeconds(millionTotal));
                }
            }
        }
    }
}
