package com.github.cao.awa.modmdo.mixins.voxel.shape;

import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import it.unimi.dsi.fastutil.doubles.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.util.shape.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;

@Mixin(VoxelShapes.class)
public abstract class VoxelShapesMixin {
    private static final Map<Double, Map<Double, Integer>> cachedBitResolution = new Object2ObjectLinkedOpenHashMap<>();
    private static final double[] CACHED_SMALLER = EntrustParser.operation(new double[4], ints -> {
        for (int i = 0; i < 4; ++ i) {
            ints[i] = 1.0E-7D * (1 << i);
        }
    });
    private static final int[] CACHED_BIT = EntrustParser.operation(new int[4], ints -> {
        for (int i = 0; i < 4; ++ i) {
            ints[i] = 1 << i;
        }
    });
    @Shadow
    @Final
    private static VoxelShape FULL_CUBE;
    @Shadow
    @Final
    private static VoxelShape EMPTY;

    @Inject(method = "cuboid(DDDDDD)Lnet/minecraft/util/shape/VoxelShape;", at = @At("HEAD"), cancellable = true)
    private static void cuboid(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, CallbackInfoReturnable<VoxelShape> cir) {
        if (minX > maxX || minY > maxY || minZ > maxZ) {
            throw new IllegalArgumentException("The min values need to be smaller or equals to the max values");
        } else {
            cir.setReturnValue(fastCuboidUnchecked(minX, minY, minZ, maxX, maxY, maxZ));
        }
    }

    private static VoxelShape fastCuboidUnchecked(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        if (maxX - minX < 1.0E-7 || maxY - minY < 1.0E-7 || maxZ - minZ < 1.0E-7) {
            return EMPTY;
        } else {
            int i = fastFindRequiredBitResolution(minX, maxX);
            int j = fastFindRequiredBitResolution(minY, maxY);
            int k = fastFindRequiredBitResolution(minZ, maxZ);
            if (i > - 1 && j > - 1 && k > - 1) {
                if (i == 0 && j == 0 && k == 0) {
                    return FULL_CUBE;
                } else {
                    int l = CACHED_BIT[i];
                    int m = CACHED_BIT[j];
                    int n = CACHED_BIT[k];
                    BitSetVoxelSet bitSetVoxelSet = BitSetVoxelSet.create(l, m, n, (int) Math.round(minX * l), (int) Math.round(minY * m), (int) Math.round(minZ * n), (int) Math.round(maxX * l), (int) Math.round(maxY * m), (int) Math.round(maxZ * n));
                    return EntrustParser.tryCreate(() -> SimpleVoxelShape.class.getDeclaredConstructor(VoxelSet.class // parameter types
                    ).newInstance(bitSetVoxelSet // parameters
                    ), null);
                }
            } else {
                return EntrustParser.tryCreate(() -> ArrayVoxelShape.class.getDeclaredConstructor(VoxelSet.class, // parameter types
                        DoubleList.class, // parameter types
                        DoubleList.class, // parameter types
                        DoubleList.class // parameter types
                ).newInstance(((VoxelShapeInterface) FULL_CUBE).getVoxels(), // parameters
                        DoubleArrayList.wrap(new double[]{minX, maxX}, 2), // parameters
                        DoubleArrayList.wrap(new double[]{minY, maxY}, 2), // parameters
                        DoubleArrayList.wrap(new double[]{minZ, maxZ}, 2) // parameters
                ), null);
            }
        }
    }

    private static int fastFindRequiredBitResolution(double min, double max) {
        if (min < - 1.0E-7D || max > 1.0000001D) {
            return - 1;
        }
        Map<Double, Integer> cached = null;
        if (cachedBitResolution.containsKey(min)) {
            cached = cachedBitResolution.get(min);
            if (cached.containsKey(max)) {
                return cached.get(max);
            }
        }
        for (int i = 0; i < 4; ++ i) {
            int j = CACHED_BIT[i];
            double d = min * j;
            double e = max * j;
            boolean bl = Math.abs(d - Math.round(d)) < CACHED_SMALLER[i];
            boolean bl2 = Math.abs(e - Math.round(e)) < CACHED_SMALLER[i];
            if (bl && bl2) {
                if (cached != null) {
                    cached.put(max, i);
                } else {
                    cached = new Object2ObjectLinkedOpenHashMap<>();
                    cached.put(max, i);
                    cachedBitResolution.put(min, cached);
                }
                return i;
            }
        }

        return - 1;
    }

    @Inject(method = "cuboidUnchecked", at = @At("HEAD"), cancellable = true)
    private static void fastCuboidUnchecked(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, CallbackInfoReturnable<VoxelShape> cir) {
        cir.setReturnValue(fastCuboidUnchecked(minX, minY, minZ, maxX, maxY, maxZ));
    }
}
