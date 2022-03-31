package com.github.zhuaidadaya.modmdo.commands;

import com.github.zhuaidadaya.modmdo.system.SystemUtil;
import com.github.zhuaidadaya.modmdo.usr.User;
import com.github.zhuaidadaya.modmdo.utils.player.PlayerUtil;
import com.github.zhuaidadaya.modmdo.utils.times.TimeUtil;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

import static com.github.zhuaidadaya.modmdo.storage.Variables.*;
import static net.minecraft.server.command.CommandManager.literal;

public class AnalyzerCommand extends SimpleCommandOperation implements SimpleCommand {
    @Override
    public void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("analyzer").then(literal("vec").executes(vec -> {
                if (commandApplyToPlayer(MODMDO_COMMAND_ANALYZER, getPlayer(vec), this, vec)) {
                    ServerPlayerEntity player = getPlayer(vec);

                    sendFeedback(vec, formatVecMessage(player.getPos(), player.getRotationClient(), dimensionTips.getDimension(player)));

                }
                return 0;
            })).then(literal("onlineTime").executes(onlineTime -> {
                if (commandApplyToPlayer(11, getPlayer(onlineTime), this, onlineTime)) {
                    ServerPlayerEntity player = getPlayer(onlineTime);

                    sendFeedback(onlineTime, formatOnlineTime(player));
                }
                return 0;
            })).then(literal("gameOnlineTime").executes(onlineTime -> {
                if (commandApplyToPlayer(11, getPlayer(onlineTime), this, onlineTime)) {
                    ServerPlayerEntity player = getPlayer(onlineTime);

                    sendFeedback(onlineTime, formatGameOnlineTime(player));
                }
                return 0;
            })).then(literal("server").then(literal("cpu").executes(cpu -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_SERVER, getPlayer(cpu), this, cpu)) {
                    new Thread(() -> {
                        sendFeedback(cpu, new TranslatableText("server.cpu", SystemUtil.getCpuTotalUsed() + "(Used)", SystemUtil.getCpuWait() + "(Wait)"));
                    }).start();
                }
                return 1;
            })).then(literal("memory").executes(memory -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_SERVER, getPlayer(memory), this, memory)) {
                    MemoryMXBean memoryMx = ManagementFactory.getMemoryMXBean();
                    MemoryUsage memoryUsage = memoryMx.getHeapMemoryUsage();

                    float usedMemory = memoryUsage.getUsed() / 1024f / 1024f;
                    float totalMemory = memoryUsage.getMax() / 1024f / 1024f;

                    String formatMemoryTag = "§" + (totalMemory / usedMemory < 0.9f ? "d" : (totalMemory / usedMemory < 0.7f ? "c" : "a"));

                    sendFeedback(memory, new TranslatableText("server.memory", formatMemoryTag + totalMemory,formatMemoryTag + usedMemory ));
                }
                return - 1;
            })).then(literal("tps").executes(tps -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_SERVER, getPlayer(tps), this, tps)) {
                    new Thread(() -> {
                        MinecraftServer server = getServer(tps);

                        float tickPerSecondTarget = 0;
                        float mspt = 0;

                        int snapTicks = 20;

                        for(int i = 0; i < snapTicks; i++) {
                            long time = server.getTimeReference();
                            while(time == server.getTimeReference()) {
                                try {
                                    Thread.sleep(1);
                                } catch (InterruptedException e) {

                                }
                            }
                            tickPerSecondTarget += server.getTimeReference() - time;
                        }

                        tickPerSecondTarget = 1000.0f / (tickPerSecondTarget / snapTicks);

                        int i = 0;
                        for(long tick : server.lastTickLengths) {
                            i++;
                            mspt += tick;
                            if(i > snapTicks) {
                                break;
                            }
                        }

                        mspt = mspt / snapTicks / 1000000;
                        String formatTickTime = ("§e" + mspt);

                        float tickPerSecond = Math.min(tickPerSecondTarget, 1000 / mspt);

                        String formatTickPerSecond = "§" + (tickPerSecond == tickPerSecondTarget ? "a" : tickPerSecondTarget / tickPerSecond > 1.2 ? "d" : "c");

                        sendFeedback(tps, new TranslatableText("server.tps", formatTickTime.substring(0, (formatTickTime.indexOf(".") > 0 ? formatTickTime.indexOf(".") + 2 : formatTickTime.length())) , formatTickPerSecond + tickPerSecondTarget, formatTickPerSecond + (tickPerSecond)));
                    }).start();
                }
                return 2;
            }))));
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
