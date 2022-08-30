package com.github.cao.awa.modmdo.mixins.voxel.shape;

import net.minecraft.util.shape.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(VoxelSet.class)
public abstract class VoxelSetMixin {
    @Shadow @Final protected int sizeX;

    @Shadow @Final protected int sizeY;

    @Shadow @Final protected int sizeZ;

    @Shadow public abstract boolean contains(int x, int y, int z);

    @Shadow public abstract boolean inBoundsAndContains(int x, int y, int z);

    @Inject(method = "inBoundsAndContains(III)Z", at = @At("HEAD"), cancellable = true)
    private void fastBoundsAndContains(int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(fastBoundsAndContains(x, y, z));
    }

    private boolean fastBoundsAndContains(int x, int y, int z) {
        return x > - 1 && y > - 1 && z > - 1 && x < this.sizeX && y < this.sizeY && z < this.sizeZ && this.contains(x, y, z);
    }
}
