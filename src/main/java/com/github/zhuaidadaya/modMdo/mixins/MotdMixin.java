package com.github.zhuaidadaya.modMdo.mixins;

import net.minecraft.server.ServerMetadata;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.github.zhuaidadaya.modMdo.Storage.Variables.motd;
import static com.github.zhuaidadaya.modMdo.Storage.Variables.server;

@Mixin(ServerMetadata.class)
public class MotdMixin {
    @Inject(method = "getDescription", at = @At("HEAD"), cancellable = true)
    private void getDescription(CallbackInfoReturnable<Text> callbackInfoReturnable) {
        if(server != null) {
            callbackInfoReturnable.setReturnValue(new LiteralText(motd));
            callbackInfoReturnable.cancel();
        }
    }
}

