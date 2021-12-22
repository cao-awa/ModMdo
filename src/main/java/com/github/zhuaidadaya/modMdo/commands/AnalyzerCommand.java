package com.github.zhuaidadaya.modMdo.commands;

import com.github.zhuaidadaya.MCH.times.TimeType;
import com.github.zhuaidadaya.MCH.times.Times;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.text.TranslatableText;

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
            })));
        });
    }
}
