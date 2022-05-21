package com.github.zhuaidadaya.modmdo.subscribable;

import com.github.zhuaidadaya.modmdo.storage.*;
import com.github.zhuaidadaya.modmdo.utils.command.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.operational.count.*;
import com.google.common.util.concurrent.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;

import java.util.*;

import static com.github.zhuaidadaya.modmdo.storage.Variables.*;

public class TickPerSecondAnalyzer extends SimpleCommandOperation {
    private final Collection<TargetCountLong<ServerPlayerEntity>> subs = new ObjectArrayList<>();
    private Thread tpsThread;
    private boolean stopTpsTest = false;
    private final AtomicDouble tps = new AtomicDouble(Double.NEGATIVE_INFINITY);
    private final AtomicDouble real = new AtomicDouble(Double.NEGATIVE_INFINITY);
    private final AtomicDouble target = new AtomicDouble(Double.POSITIVE_INFINITY);

    public void stop() {
        this.stopTpsTest = true;
    }

    public AtomicDouble getTps() {
        return tps;
    }

    public AtomicDouble getReal() {
        return real;
    }

    public AtomicDouble getTarget() {
        return target;
    }

    public void init(MinecraftServer server, long target) {
        if (! isRunning()) {
            stopTpsTest = false;
            subs.clear();
            tpsThread = new Thread(() -> {
                int ticks = 0;
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

                    if (ticks > Integer.MAX_VALUE - 1) {
                        ticks = 0;
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

                        this.target.set(targetTps);
                        this.real.set(realTps);
                        this.tps.set(tps);

                        String formatTps = "§" + (tps == targetTps ? "a" : targetTps / tps > 1.2 ? "d" : "c");

                        String countTicks = Integer.toString(ticks);
                        String realMspt = fractionDigits2.format(tickTime);
                        String subTps = fractionDigits2.format(subMspt / 1000d);
                        String tpsCurrent = formatTps + fractionDigits2.format(tps);
                        String tpsTarget = fractionDigits2.format(targetTps);
                        for (TargetCountLong<ServerPlayerEntity> counter : subs) {
                            ServerPlayerEntity player = counter.getTarget();
                            if (player.networkHandler.connection.isOpen()) {
                                Variables.sendMessage(player, new LiteralText("§etick-" + countTicks + ": §b(mspt: §a[r: " + realMspt + "ms, sub: " + subTps + "ms]§b, tps: §a[r: " + tpsCurrent + "§a, target: " + tpsTarget + "]§b)"), true);
                            } else {
                                cancelSub(player);
                            }
                            counter.add();
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
        subs.removeIf(counter -> counter.getTarget() == player);
        sendMessage(player, new TranslatableText("subscribe.remove.from", "tps"), false, 20);
    }

    public void addSub(ServerPlayerEntity player, long ticks) {
        subs.add(new TargetCountLong<>(player, ticks));
        sendMessage(player, new TranslatableText("subscribe.add.to", "tps"), false, 20);
    }

    public boolean hasSub(ServerPlayerEntity player) {
        for (TargetCountLong<ServerPlayerEntity> counter : subs) {
            if (counter.getTarget() == player) {
                return true;
            }
        }
        return false;
    }
}

