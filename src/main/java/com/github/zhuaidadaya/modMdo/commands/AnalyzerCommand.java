package com.github.zhuaidadaya.modMdo.commands;

import com.github.zhuaidadaya.MCH.times.TimeType;
import com.github.zhuaidadaya.MCH.times.Times;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import static com.github.zhuaidadaya.modMdo.storage.Variables.*;
import static net.minecraft.server.command.CommandManager.literal;

public class AnalyzerCommand {
    public void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("analyzer").then(literal("tick").executes(tickAnalyze -> {
                if(shortAnalyze & !enableTickAnalyzer) {
                    new Thread(() -> {
                        analyzedTick = 0;

                        tickAnalyzerFile = "logs/tick_analyzer/" + Times.getTime(TimeType.ALL) + ".log";
                        tickAnalyze.getSource().sendFeedback(new TranslatableText("analyzer.result", tickAnalyzerFile), true);

                        tickAnalyze.getSource().sendFeedback(new TranslatableText("analyzer.started"), true);

                        enableTickAnalyzer = true;

                        while(enableTickAnalyzer) {
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {

                            }
                        }

                        tickAnalyze.getSource().sendFeedback(new TranslatableText("analyzer.finished"), true);
                    }).start();
                } else {
                    tickAnalyze.getSource().sendFeedback(new TranslatableText("analyzer.tasking"), true);
                }

                return 1;
            })).then(literal("vec").executes(defaultBackup -> {
                ServerCommandSource source = defaultBackup.getSource();
                ServerPlayerEntity player = source.getPlayer();

                DimensionTips dimensionTips = new DimensionTips();

                player.sendMessage(formatVecMessage(player.getPos(), player.getRotationClient(), dimensionTips.getDimension(player)), false);

                return 0;
            })));
        });
    }

    public TranslatableText formatVecMessage(Vec3d vec3d, Vec2f vec2f, String dimension) {
        return new TranslatableText("command.vec.format", vec3d.x, vec3d.y, vec3d.z, vec2f.x, vec2f.y, dimension);
    }
}
