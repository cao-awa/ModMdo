package com.github.zhuaidadaya.modmdo.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.github.zhuaidadaya.modmdo.storage.Variables.*;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow
    private static MinecraftClient instance;

    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    private boolean handleInp = true;

    @Inject(method = "tick", at = @At("RETURN"))
    private void jumpServer(CallbackInfo ci) {
        if(connectTo) {
            handleInp = false;
            connectTo = false;

            try {
                if(instance != null & servers.getServers().size() > 0) {
                    servers.jump(jump, instance);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Inject(method = "handleInputEvents", at = @At("HEAD"), cancellable = true)
    private void handleInputEvents(CallbackInfo ci) {
        if(! handleInp || player == null) {
            ci.cancel();
            handleInp = true;
        }
    }

    @Inject(method = "setScreen",at = @At("HEAD"),cancellable = true)
    public void setScreen(Screen screen, CallbackInfo ci) {
        try {
            if(connectTo) {
                ci.cancel();
            }
        } catch (Exception e) {

        }
    }
}