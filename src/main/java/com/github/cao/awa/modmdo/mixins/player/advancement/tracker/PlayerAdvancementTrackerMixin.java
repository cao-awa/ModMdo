package com.github.cao.awa.modmdo.mixins.player.advancement.tracker;

import com.github.cao.awa.modmdo.utils.times.*;
import net.minecraft.advancement.*;
import net.minecraft.server.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(PlayerAdvancementTracker.class)
public class PlayerAdvancementTrackerMixin {
    private long time = 0;

    @Inject(method = "load", at = @At("HEAD"))
    public void startLoad(ServerAdvancementLoader advancementLoader, CallbackInfo ci) {
        time = TimeUtil.millions();
        System.out.println("start load");
        for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
            System.out.println(stackTraceElement);
        }
    }

    @Inject(method = "load", at = @At("RETURN"))
    public void doneLoad(ServerAdvancementLoader advancementLoader, CallbackInfo ci) {
        System.out.println("load done in " + TimeUtil.processMillion(time) + "ms");
    }
}
