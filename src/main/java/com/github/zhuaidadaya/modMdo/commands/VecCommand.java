package com.github.zhuaidadaya.modMdo.commands;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import static net.minecraft.server.command.CommandManager.literal;

public class VecCommand {
    public void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("vec").executes(defaultBackup -> {
                ServerCommandSource source = defaultBackup.getSource();
                ServerPlayerEntity player = source.getPlayer();

                DimensionTips dimensionTips = new DimensionTips();

                player.sendMessage(formatVecMessage(player.getPos(), player.getRotationClient(), dimensionTips.getDimension(player)), false);

                return 0;
            }));
        });
    }

    public TranslatableText formatVecMessage(Vec3d vec3d, Vec2f vec2f, String dimension) {
        return new TranslatableText("command.vec.format", vec3d.x, vec3d.y, vec3d.z, vec2f.x, vec2f.y, dimension);
    }
}
