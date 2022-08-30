package com.github.cao.awa.modmdo.mixins.voxel.shape;

import com.github.zhuaidadaya.rikaishinikui.handler.universal.receptacle.*;
import it.unimi.dsi.fastutil.doubles.*;
import net.minecraft.util.function.*;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(VoxelShape.class)
public abstract class VoxelShapeMixin {
    @Shadow
    @Final
    protected VoxelSet voxels;

    @Inject(method = "simplify", at = @At("HEAD"), cancellable = true)
    private void simplify(CallbackInfoReturnable<VoxelShape> cir) {
        Receptacle<VoxelShape> voxelShapes = new Receptacle<>(VoxelShapes.empty());
        this.forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> {
            voxelShapes.set(VoxelShapes.combine(voxelShapes.get(), VoxelShapes.cuboid(minX, minY, minZ, maxX, maxY, maxZ), (BooleanBiFunction.OR)));
        });
        cir.setReturnValue(voxelShapes.get());
    }

    @Shadow
    public abstract void forEachBox(VoxelShapes.BoxConsumer boxConsumer);

    @Shadow
    protected abstract DoubleList getPointPositions(Direction.Axis axis);
}
