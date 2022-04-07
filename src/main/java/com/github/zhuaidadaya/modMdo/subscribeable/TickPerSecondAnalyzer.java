package com.github.zhuaidadaya.modmdo.subscribeable;

import com.github.zhuaidadaya.modmdo.commands.SimpleCommandOperation;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

import java.util.Collection;

import static com.github.zhuaidadaya.modmdo.storage.Variables.*;

public class TickPerSecondAnalyzer extends SimpleCommandOperation {
    private final Collection<ServerPlayerEntity> subs = new ObjectArrayList<>();
    private Thread tpsThread;
    private boolean stopTpsTest = false;

    public void stop() {
        this.stopTpsTest = true;
    }

    public void init(MinecraftServer server, long target) {
        if (! isRunning()) {
            stopTpsTest = false;
            subs.clear();
            tpsThread = new Thread(() -> {
                float ticks = 0;
                float tickTime = - 1;
                long subMspt;
                long planTickTimeTarget = 50;
                int lastTicks;

                for (long i = 0; (i < target * 2 || target == - 1) && ! stopTpsTest; i++) {
                    lastTicks = server.getTicks();
                    long time = server.getTimeReference();
                    boolean freezing = tickTime == server.getTickTime();
                    boolean skip = freezing;
                    while (time == server.getTimeReference() || freezing) {
                        if (! server.isRunning()) {
                            return;
                        }
                        freezing = tickTime == server.getTickTime();
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {

                        }
                    }

                    if (planTickTimeTarget != server.getTimeReference() - time) {
                        if (skip) {
                            tickTime = server.getTickTime();
                            ticks++;
                        }
                        planTickTimeTarget = (server.getTimeReference() - time) / Math.max(1, server.getTicks() - lastTicks);
                    }

                    if (! skip) {
                        tickTime = server.getTickTime();
                        ticks++;
                    }

                    subMspt = 0;
                    try {
                        for (long sub : server.lastTickLengths) {
                            subMspt += sub / 100000d;
                        }
                        float targetTps = 1000.0f / planTickTimeTarget;
                        float realTps = 1000f / (subMspt / 1000f);
                        float tps = Math.min(targetTps, realTps);
                        String formatTps = "§" + (tps == targetTps ? "a" : targetTps / tps > 1.2 ? "d" : "c");

                        String countTicks = fractionDigits0.format(ticks);
                        String realMspt = fractionDigits2.format(tickTime);
                        String subTps = fractionDigits2.format(subMspt / 1000d);
                        String tpsCurrent = formatTps + fractionDigits2.format(tps);
                        String tpsTarget = fractionDigits2.format(targetTps);
                        for (ServerPlayerEntity player : subs) {
                            if (player.networkHandler.connection.isOpen()) {
                                sendMessageToPlayer(player, new LiteralText("§etick-" + countTicks + ": §b(mspt: §a[r: " + realMspt + "ms, sub: " + subTps + "ms]§b, tps: §a[r: " + tpsCurrent + "§a, target: " + tpsTarget + "]§b)"), true);
                            } else {
                                cancelSub(player);
                            }
                        }
                    } catch (Exception e) {

                    }
                }

                stopTpsTest = false;
            });
            tpsThread.start();
            stopTpsTest = false;
        }
    }

    public boolean isRunning() {
        return tpsThread != null && tpsThread.isAlive();
    }

    public void cancelSub(ServerPlayerEntity player) {
        subs.remove(player);
    }

    public void addSub(ServerPlayerEntity player) {
        subs.add(player);
    }

    public boolean hasSub(ServerPlayerEntity player) {
        return subs.contains(player);
    }
}
