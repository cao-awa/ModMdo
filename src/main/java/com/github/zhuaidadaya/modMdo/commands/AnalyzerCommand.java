package com.github.zhuaidadaya.modMdo.commands;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import static com.github.zhuaidadaya.modMdo.storage.Variables.*;
import static net.minecraft.server.command.CommandManager.literal;

public class AnalyzerCommand extends SimpleCommandOperation implements SimpleCommand {
    @Override
    public void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("analyzer").then(literal("vec").executes(vec -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_ANALYZER, getPlayer(vec), this, vec)) {
                    ServerPlayerEntity player = getPlayer(vec);

                    sendFeedback(vec, formatVecMessage(player.getPos(), player.getRotationClient(), dimensionTips.getDimension(player)));

                }
                return 0;
            })));
        });
    }

    public TranslatableText formatVecMessage(Vec3d vec3d, Vec2f vec2f, String dimension) {
        return new TranslatableText("command.vec.format", vec3d.x, vec3d.y, vec3d.z, vec2f.x, vec2f.y, dimension);
    }
}
