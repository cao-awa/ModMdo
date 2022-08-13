package com.github.cao.awa.modmdo.mixins.nbt;

import net.minecraft.nbt.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(NbtTagSizeTracker.class)
public class NbtTagSizeTrackerMixin {
    @Shadow private long allocatedBytes;

    @Inject(method = "add", at = @At("HEAD"), cancellable = true)
    private void add(long bits, CallbackInfo ci) {
        this.allocatedBytes += bits / 8L;
        ci.cancel();
    }
}
