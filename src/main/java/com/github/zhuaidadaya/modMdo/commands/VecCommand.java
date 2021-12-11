package com.github.zhuaidadaya.modMdo.commands;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.minecraft.server.command.CommandManager.literal;

public class VecCommand {
    public void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("vec").executes(defaultBackup -> {
                ServerCommandSource source = defaultBackup.getSource();
                ServerPlayerEntity player = source.getPlayer();

                DimensionTips dimensionTips = new DimensionTips();

                System.out.println("Vec3d-x: " + player.getPos().x);
                System.out.println("Vec3d-y: " + player.getPos().y);
                System.out.println("Vec3d-z: " + player.getPos().z);
                System.out.println("Vec2f-x: " + player.getRotationClient().x);
                System.out.println("Vec2f-y: " + player.getRotationClient().y);
                System.out.println("Dimension: " + dimensionTips.getDimension(player));
                System.out.println("more?");

                return 0;
            }));
        });
    }
}
