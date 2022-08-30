package com.github.cao.awa.modmdo.mixins.voxel.shape;

import net.minecraft.util.shape.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.*;

@Mixin(VoxelSet.class)
public interface VoxelSetInterface {
    @Accessor("sizeX")
    int getSizeX();

    @Accessor("sizeZ")
    int getSizeZ();

    @Accessor("sizeY")
    int getSizeY();

    @Invoker("inBoundsAndContains")
    boolean inBoundsAndContains(int x, int y, int z);
}
