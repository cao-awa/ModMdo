package com.github.zhuaidadaya.modMdo.commands;

import com.github.zhuaidadaya.modMdo.system.SystemUtil;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TranslatableText;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

import static com.github.zhuaidadaya.modMdo.storage.Variables.MODMDO_COMMAND_SERVER;
import static com.github.zhuaidadaya.modMdo.storage.Variables.commandApplyToPlayer;
import static net.minecraft.server.command.CommandManager.literal;

public class ServerCommand extends SimpleCommandOperation implements SimpleCommand {
    @Override
    public void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("server").then(literal("cpu").executes(cpu -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_SERVER, getPlayer(cpu), this, cpu)) {
                    new Thread(() -> {
                        sendFeedback(cpu, new TranslatableText("server.cpu",  SystemUtil.getCpuTotalUsed() + "(Used)",  SystemUtil.getCpuWait() + "(Wait)"));
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
            })));
        });
    }
}
