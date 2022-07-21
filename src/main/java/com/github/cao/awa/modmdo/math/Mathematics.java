package com.github.cao.awa.modmdo.math;

import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.receptacle.*;
import net.minecraft.util.math.*;

public final class Mathematics extends MathHelper {
    private static final float[] SINE_HALF_TABLE = EntrustParser.operation(new float[65536], (table) -> {
        for(int i = 0; i < table.length; ++i) {
            table[i] = (float) Math.sin(i * 3.141592653589793 * 2 / 65536) / 2;
        }
    });

    public static BlockPos center(BlockPos pos1, BlockPos pos2) {
        int xW = max(pos2.getX(), pos1.getX()) - min(pos2.getX(), pos1.getX());
        int yW = max(pos2.getY(), pos1.getY()) - min(pos2.getY(), pos1.getY());
        int zW = max(pos2.getZ(), pos1.getZ()) - min(pos2.getZ(), pos1.getZ());
        int xCenter = pos1.getX() > pos2.getX() ? pos1.getX() - (xW / 2) : pos1.getX() + (xW / 2);
        int yCenter = pos1.getY() > pos2.getY() ? pos1.getY() - (yW / 2) : pos1.getY() + (yW / 2);
        int zCenter = pos1.getZ() > pos2.getZ() ? pos1.getZ() - (zW / 2) : pos1.getZ() + (zW / 2);

        return new BlockPos(xCenter, yCenter, zCenter);
    }

    public static double max(double a, double b) {
        return a > b ? a : b;
    }

    public static double min(double a, double b) {
        return a > b ? b : a;
    }

    public static int max(int a, int b) {
        return a > b ? a : b;
    }

    public static int min(int a, int b) {
        return a > b ? b : a;
    }

    public static Legacy<Double, Double> legacy(double a, double b) {
        return new Legacy<>(max(a, b), min(a, b));
    }

    public static float halfSin(float value) {
        return SINE_HALF_TABLE[(int)(value * 10430.378) & '\uffff'];
    }
}

