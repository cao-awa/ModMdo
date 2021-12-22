package com.github.zhuaidadaya.modMdo.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(World.class)
public abstract class WorldMixin implements WorldAccess, AutoCloseable {

    @Shadow
    @Final
    private static Direction[] DIRECTIONS;

    @Shadow
    public abstract BlockState getBlockState(BlockPos pos);

    /**
     * @author 草awa
     * @reason
     */
    @Overwrite
    public int getReceivedStrongRedstonePower(BlockPos pos) {
        int i = 0;
        for(Direction direction : Direction.values()) {
            if(i < 15) {
                i = Math.max(i, this.getStrongRedstonePower(pos.offset(direction), direction));
            }
        }
        return i;
    }

    @Shadow
    public abstract int getEmittedRedstonePower(BlockPos pos, Direction direction);

    /**
     * @author 草awa
     * @reason
     */
    @Overwrite
    public int getReceivedRedstonePower(BlockPos pos) {
        int i = 0;
        Direction[] var3 = DIRECTIONS;

        for(Direction direction : var3) {
            int j = this.getEmittedRedstonePower(pos.offset(direction), direction);
            if(j > 14) {
                return 15;
            }

            if(j > i) {
                i = j;
            }
        }

        return i;
    }
}
