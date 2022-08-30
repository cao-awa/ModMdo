package com.github.cao.awa.modmdo.mixins.voxel.shape;

import net.minecraft.util.shape.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;

@Mixin(BitSetVoxelSet.class)
public abstract class BitSetVoxelSetMixin {
    @Shadow @Final private BitSet storage;

    @Shadow protected abstract int getIndex(int x, int y, int z);

    @Shadow private int minX;

    @Shadow private int minY;

    @Shadow private int minZ;

    @Shadow private int maxX;

    @Shadow private int maxY;

    @Shadow private int maxZ;

    @Inject(method = "method_31941", at = @At("HEAD"), cancellable = true)
    private static void clear(VoxelSet voxelSet, VoxelSet.PositionBiConsumer positionBiConsumer, boolean bl, CallbackInfo ci) {
        clear(voxelSet, positionBiConsumer, bl);
        ci.cancel();
    }

    private static void clear(VoxelSet voxelSet, VoxelSet.PositionBiConsumer positionBiConsumer, boolean bl) {
        BitSetVoxelSetInterface bitSetVoxelSet = ((BitSetVoxelSetInterface) (VoxelSet) new BitSetVoxelSet(voxelSet));

        for (int i = 0; i < bitSetVoxelSet.getSizeX(); ++ i) {
            for (int j = 0; j < bitSetVoxelSet.getSizeY(); ++ j) {
                int k = - 1;

                for (int l = 0; l <= bitSetVoxelSet.getSizeZ(); ++ l) {
                    if (bitSetVoxelSet.inBoundsAndContains(i, j, l)) {
                        if (bl) {
                            k = k == - 1 ? l : - 1;
                        } else {
                            positionBiConsumer.consume(i, j, l, i + 1, j + 1, l + 1);
                        }
                    } else {
                        if (k == - 1) {
                            continue;
                        }
                        bitSetVoxelSet.clear(k, l, i, j);

                        int m = i;
                        int n = j;
                        while (bitSetVoxelSet.isColumnFull(k, l, m + 1, j)) {
                            bitSetVoxelSet.clear(k, l, m ++, j);
                        }

                        while (bitSetVoxelSet.isColumnFull(i, m + 1, k, l, n + 1)) {
                            for (int o = i; o <= m; ++ o) {
                                bitSetVoxelSet.clear(k, l, o, n + 1);
                            }

                            ++ n;
                        }

                        positionBiConsumer.consume(i, j, k, m + 1, n + 1, l);
                        k = - 1;
                    }
                }
            }
        }
    }

    @Inject(method = "method_31939", at = @At("HEAD"), cancellable = true)
    private static void method_31939(int sizeX, int sizeY, int sizeZ, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, CallbackInfoReturnable<BitSetVoxelSet> cir) {
        cir.setReturnValue(set(sizeX, sizeY, sizeZ, minX, minY, minZ, maxX, maxY, maxZ));
    }

    private static BitSetVoxelSet set(int sizeX, int sizeY, int sizeZ, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        BitSetVoxelSetInterface bitSetVoxelSet = ((BitSetVoxelSetInterface)(VoxelSet)new BitSetVoxelSet(sizeX, sizeY, sizeZ));
        bitSetVoxelSet.setMinX(minX);
        bitSetVoxelSet.setMinY(minY);
        bitSetVoxelSet.setMinZ(minZ);
        bitSetVoxelSet.setMaxX(maxX);
        bitSetVoxelSet.setMaxY(maxY);
        bitSetVoxelSet.setMaxZ(maxZ);

        for(int i = minX; i < maxX; ++i) {
            for(int j = minY; j < maxY; ++j) {
                for(int k = minZ; k < maxZ; ++k) {
                    bitSetVoxelSet.set(i, j, k, false);
                }
            }
        }

        return (BitSetVoxelSet) (VoxelSet) bitSetVoxelSet;
    }
}
