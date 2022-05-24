package com.github.cao.awa.modmdo.math;

import net.minecraft.util.math.*;

import static java.lang.Math.*;

public final class Mathematics {
    public static BlockPos center(BlockPos pos1, BlockPos pos2) {
        int xW = max(pos2.getX(), pos1.getX()) - min(pos2.getX(), pos1.getX());
        int yW = max(pos2.getY(), pos1.getY()) - min(pos2.getY(), pos1.getY());
        int zW = max(pos2.getZ(), pos1.getZ()) - min(pos2.getZ(), pos1.getZ());
        int xCenter = pos1.getX() > pos2.getX() ? pos1.getX() - (xW / 2) : pos1.getX() + (xW / 2);
        int yCenter = pos1.getY() > pos2.getY() ? pos1.getY() - (yW / 2) : pos1.getY() + (yW / 2);
        int zCenter = pos1.getZ() > pos2.getZ() ? pos1.getZ() - (zW / 2) : pos1.getZ() + (zW / 2);

        return new BlockPos(xCenter, yCenter, zCenter);
    }
}

