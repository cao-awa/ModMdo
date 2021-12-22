package com.github.zhuaidadaya.modMdo.mixins;

import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.Set;

@Mixin(RedstoneWireBlock.class)
public abstract class RedstoneWireBlockMixin extends Block {

    @Shadow
    @Final
    public static IntProperty POWER;
    @Shadow
    @Final
    public static Map<Direction, EnumProperty<WireConnection>> DIRECTION_TO_WIRE_CONNECTION_PROPERTY;
    @Shadow
    private boolean wiresGivePower;

    public RedstoneWireBlockMixin(Settings settings) {
        super(settings);
    }

    @Shadow
    protected abstract int increasePower(BlockState state);

    /**
     * @author 草awa
     * @reason
     */
    @Overwrite
    private void updateNeighbors(World world, BlockPos pos) {
        if(world.getBlockState(pos).isOf(this)) {
            world.updateNeighborsAlways(pos, this);

            for(Direction direction : Direction.values()) {
                world.updateNeighborsAlways(pos.offset(direction), this);
            }
        }
    }

    /**
     * @author 草awaw
     * @reason
     */
    @Overwrite
    private void updateOffsetNeighbors(World world, BlockPos pos) {
        for(Direction direction : Direction.Type.HORIZONTAL) {
            this.updateNeighbors(world, pos.offset(direction));

            BlockPos blockPos = pos.offset(direction);
            if(world.getBlockState(blockPos).isSolidBlock(world, blockPos)) {
                this.updateNeighbors(world, blockPos.up());
            } else {
                this.updateNeighbors(world, blockPos.down());
            }
        }
    }

    /**
     * @author 草二号机
     * @reason
     */
    @Overwrite
    private int getReceivedRedstonePower(World world, BlockPos pos) {
        this.wiresGivePower = false;
        int i = world.getReceivedRedstonePower(pos);
        this.wiresGivePower = true;
        int j = 0;
        if(i < 15) {
            for(Direction direction : Direction.Type.HORIZONTAL) {
                BlockPos blockPos = pos.offset(direction);
                BlockState blockState = world.getBlockState(blockPos);
                j = Math.max(j, this.increasePower(blockState));
                BlockPos blockPos2 = pos.up();
                boolean solidBlock = blockState.isSolidBlock(world, blockPos);
                if(solidBlock && ! world.getBlockState(blockPos2).isSolidBlock(world, blockPos2)) {
                    j = Math.max(j, this.increasePower(world.getBlockState(blockPos.up())));
                } else {
                    j = Math.max(j, this.increasePower(world.getBlockState(blockPos.down())));
                }
            }
        } else {
            return i;
        }
        return Math.max(i, j - 1);
    }

    /**
     * @author 草二号机
     * @reason
     */
    @Overwrite
    private void update(World world, BlockPos pos, BlockState state) {
        int i = getReceivedRedstonePower(world, pos);
        if(state.get(POWER) != i) {
            if(world.getBlockState(pos) == state) {
                world.setBlockState(pos, state.with(POWER, i), 2);
            }

            Set<BlockPos> set = Sets.newHashSet();
            set.add(pos);

            for(Direction direction : Direction.values()) {
                set.add(pos.offset(direction));
            }

            for(BlockPos blockPos : set) {
                world.updateNeighborsAlways(blockPos, this);
            }
        }
    }
}
