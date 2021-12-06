package com.github.zhuaidadaya.modMdo;

import com.github.zhuaidadaya.MCH.Utils.Config.ConfigUtil;
import com.github.zhuaidadaya.modMdo.Commands.DimensionHereCommand;
import com.github.zhuaidadaya.modMdo.Commands.HereCommand;
import com.github.zhuaidadaya.modMdo.Commands.ModMdoUserCommand;
import com.github.zhuaidadaya.modMdo.Commands.ProjectCommand;
import com.github.zhuaidadaya.modMdo.Lang.LanguageDictionary;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

import static com.github.zhuaidadaya.modMdo.Storage.Variables.*;
import static net.minecraft.server.command.CommandManager.literal;

public class ModMdo implements ModInitializer {
    @Override
    public void onInitialize() {
        LOGGER.info("loading for ModMdo");

        config = new ConfigUtil("config/","ModMdo.mhf",entrust);

        languageDictionary = new LanguageDictionary("/format/format.json");

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("here").executes(context -> {
                return new HereCommand().here(context);
            }));
            dispatcher.register(literal("dhere").executes(context -> {
                return new DimensionHereCommand().dhere(context);
            }));
        });

        new ProjectCommand().project();
        new ModMdoUserCommand().user();
    }
}
