package com.github.cao.awa.modmdo.mixins.voxel.shape;

import net.minecraft.util.shape.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.*;

@Mixin(BitSetVoxelSet.class)
public interface BitSetVoxelSetInterface extends VoxelSetInterface {
    @Invoker("method_31942")
    void clear(int i, int j, int k, int l);

    @Invoker("isColumnFull")
    boolean isColumnFull(int i, int j, int k, int l);

    @Invoker("method_31938")
    boolean isColumnFull(int i, int j, int k, int l, int m);

    @Accessor("minX")
    int getMinX();
    @Accessor("minY")
    int getMinY();
    @Accessor("minZ")
    int getMinZ();

    @Accessor("minX")
    void setMinX(int minX);
    @Accessor("minY")
    void setMinY(int minY);
    @Accessor("minZ")
    void setMinZ(int minZ);

    @Accessor("maxX")
    void setMaxX(int maxX);
    @Accessor("maxY")
    void setMaxY(int maxY);
    @Accessor("maxZ")
    void setMaxZ(int maxZ);

    @Invoker("set")
    void set(int x, int y, int z, boolean update);
}
