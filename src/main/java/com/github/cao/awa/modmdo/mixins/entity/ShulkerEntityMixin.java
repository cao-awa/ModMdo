package com.github.cao.awa.modmdo.mixins.entity;

import net.minecraft.entity.*;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.world.*;
import org.spongepowered.asm.mixin.*;

@Mixin(ShulkerEntity.class)
public abstract class ShulkerEntityMixin extends GolemEntity implements Monster {
//    private static final byte ZERO_BYTE = 0;
//    @Shadow
//    @Final
//    protected static TrackedData<Byte> PEEK_AMOUNT;
//    @Shadow
//    private float openProgress;
//    @Shadow
//    private float prevOpenProgress;
//
    protected ShulkerEntityMixin(EntityType<? extends GolemEntity> entityType, World world) {
        super(entityType, world);
    }
//
//    // take over for tryTeleport()
//    @Inject(method = "tryTeleport", at = @At("HEAD"), cancellable = true)
//    private void tryTeleport(CallbackInfoReturnable<Boolean> cir) {
//        if (testingShulker) {
//            if (hasAi() && this.isAlive()) {
//                for(int i = 0; i < 5; ++i) {
//                    BlockPos blockPos2 = this.getBlockPos().add(MathHelper.nextBetween(this.random, - 8, 8), MathHelper.nextBetween(this.random, - 8, 8), MathHelper.nextBetween(this.random, - 8, 8));
//                    if (this.world.isSpaceEmpty(this, new Box(blockPos2).contract(1.0E-6D))) {
//                        if (blockPos2.getY() > this.world.getBottomY() && this.world.isAir(blockPos2) && this.world.getWorldBorder().contains(blockPos2)) {
//                            Direction direction = this.findAttachSide(blockPos2);
//                            if (direction != null) {
//                                this.detach();
//                                this.setAttachedFace(direction);
//                                this.playSound(SoundEvents.ENTITY_SHULKER_TELEPORT, 1, 1);
//                                this.setPosition(blockPos2.getX() + 0.5, blockPos2.getY(), blockPos2.getZ() + 0.5);
//                                this.dataTracker.set(PEEK_AMOUNT, ZERO_BYTE);
//                                this.setTarget(null);
//                                cir.setReturnValue(true);
//                            }
//                        }
//                    }
//                }
//            }
//            cir.setReturnValue(false);
//        }
//    }
//
//    @Shadow
//    @Nullable
//    protected abstract Direction findAttachSide(BlockPos pos);
//
//    // entity has AI or disabled?
//    private boolean hasAi() {
//        return (this.dataTracker.get(((MobEntityInterface) this).getMOB_FLAGS()) & 1) == 0;
//    }
//
//    // take over for findAttachSide()
//    @Inject(method = "findAttachSide", at = @At("HEAD"), cancellable = true)
//    private void findAttachSide(BlockPos pos, CallbackInfoReturnable<Direction> cir) {
//        for (Direction direction : Direction.values()) {
//            if (this.canStay(pos, direction)) {
//                cir.setReturnValue(direction);
//            }
//        }
//        cir.setReturnValue(null);
//    }
//
//    @Shadow
//    abstract boolean canStay(BlockPos pos, Direction direction);
//
//    // simplify canStay()
//    @Inject(method = "canStay", at = @At("HEAD"), cancellable = true)
//    private void canStay1(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
//        if (this.canStay2(pos)) {
//            cir.setReturnValue(false);
//        } else {
//            Direction direction2 = direction.getOpposite();
//            cir.setReturnValue(this.world.isDirectionSolid(pos.offset(direction), this, direction2) && this.world.isSpaceEmpty(this, stretch(direction2).offset(pos).contract(1E-6)));
//        }
//    }
//
//    // simplify method_33346() linked to method_33347()
//    private static Box stretch(Direction direction) {
//        return new Box(BlockPos.ORIGIN).stretch(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ()).shrink(0, 0, 0);
//    }
//
//    // simplify method_33351()
//    private boolean canStay2(BlockPos pos) {
//        BlockState blockState = this.world.getBlockState(pos);
//        return ! (blockState.isAir() || blockState.isOf(Blocks.MOVING_PISTON) && pos.equals(this.getBlockPos()));
//    }
//
//    // take over for moveEntities()
//    @Inject(method = "moveEntities", at = @At("HEAD"), cancellable = true)
//    private void moveEntities(CallbackInfo ci) {
//        if (testingShulker) {
//            this.refreshPosition();
//            float progress = halfSine(this.openProgress);
//            float prev = halfSine(this.prevOpenProgress);
//            Direction direction = this.getAttachedFace().getOpposite();
//            float pr = progress - prev;
//            if (pr > 0) {
//                List<Entity> list = this.world.getOtherEntities(this, stretch(direction, prev, progress).offset(this.getX() - 0.5, this.getY(), this.getZ() - 0.5), EntityPredicates.EXCEPT_SPECTATOR.and((entityx) -> ! entityx.isConnectedThroughVehicle(this)));
//
//                for (Entity entity : list) {
//                    if (entity.noClip) {
//                        continue;
//                    }
//                    entity.move(MovementType.SHULKER, new Vec3d(pr * direction.getOffsetX(), pr * direction.getOffsetY(), pr * direction.getOffsetZ()));
//                }
//            }
//            ci.cancel();
//        }
//    }
//
//    @Shadow
//    public abstract Direction getAttachedFace();
//
//    @Shadow
//    protected abstract void setAttachedFace(Direction face);
//
//    // method_33347() for other method
//    private static Box stretch(Direction direction, float prev, float progress) {
//        double max = Mathematics.max(prev, progress);
//        double shrinking = Mathematics.min(prev, progress) + 1;
//        return new Box(BlockPos.ORIGIN).stretch(direction.getOffsetX() * max, direction.getOffsetY() * max, direction.getOffsetZ() * max).shrink(- direction.getOffsetX() * shrinking, - direction.getOffsetY() * shrinking, - direction.getOffsetZ() * shrinking);
//    }
//
//    private static float halfSine(float f) {
//        return 0.5F - Mathematics.halfSin((0.5F + f) * 3.1415927F);
//    }
}
