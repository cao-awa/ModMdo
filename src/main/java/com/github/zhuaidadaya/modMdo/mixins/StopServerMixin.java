package com.github.zhuaidadaya.modMdo.mixins;


import net.minecraft.server.rcon.RconBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.github.zhuaidadaya.modMdo.Storage.Variables.LOGGER;
import static com.github.zhuaidadaya.modMdo.Storage.Variables.server;

@Mixin(RconBase.class)
public class StopServerMixin {

    /**
     * @author
     */
    @Inject(at = @At("HEAD"),method = "stop", cancellable = true)
    public void stop(CallbackInfo ci) {
        boolean in = false;
        if(server.isRunning()) {
            in = true;
            if(server.getThread() != null) {
                int i = 0;

                while(server.getThread().isAlive()) {
                    try {
                        LOGGER.info("failed to stop server, ModMdo trying stop again");

                        server.stop(false);
                        i++;

                        if(i > 5) {
                            Runtime.getRuntime().exit(0);
                            LOGGER.info("failed to stop server in 5 times try, ModMdo trying force stop task");
                        }
                    } catch(Exception ex) {

                    }
                }
            }
        }

        if(in) {
            Runtime.getRuntime().exit(0);
            LOGGER.info("ModMdo cannot stop server! it will waiting for a long time!");
        }
        ci.cancel();
    }
}
