package com.github.cao.awa.modmdo.mixins.voxel.shape;

import net.minecraft.util.shape.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.*;

@Mixin(VoxelShape.class)
public interface VoxelShapeInterface {
    @Accessor("voxels")
    VoxelSet getVoxels();
}
