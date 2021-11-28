package com.github.zhuaidadaya.modMdo;

import com.github.zhuaidadaya.modMdo.commands.DimensionHereCommand;
import net.fabricmc.api.ModInitializer;
import com.github.zhuaidadaya.modMdo.commands.HereCommand;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.minecraft.server.command.CommandManager.literal;

public class ModMdo implements ModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LogManager.getLogger("modid");

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        LOGGER.info("Hello Fabric world!");

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("here").executes(context -> {
                return new HereCommand().here(context);
            }));
            dispatcher.register(literal("dhere").executes(context -> {
                return new DimensionHereCommand().dhere(context);
            }));
        });
    }
}
