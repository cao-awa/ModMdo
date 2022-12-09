package com.github.cao.awa.modmdo.mixins.command.summon;

import com.github.cao.awa.modmdo.utils.text.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.operational.*;
import com.mojang.brigadier.*;
import com.mojang.brigadier.exceptions.*;
import net.minecraft.command.*;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.*;
import net.minecraft.nbt.*;
import net.minecraft.server.command.*;
import net.minecraft.server.world.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(SummonCommand.class)
public class SummonCommandMixin {
    @Shadow
    @Final
    private static SimpleCommandExceptionType INVALID_POSITION_EXCEPTION;

    @Shadow
    @Final
    private static SimpleCommandExceptionType FAILED_EXCEPTION;

    @Shadow
    @Final
    private static SimpleCommandExceptionType FAILED_UUID_EXCEPTION;

    @Inject(method = "register", at = @At("HEAD"))
    private static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CallbackInfo ci) {

    }

    private static int execute(ServerCommandSource source, Identifier identifier, Vec3d pos, NbtCompound nbt, boolean initialize, int count) throws CommandSyntaxException {
        if (count > 1) {
            OperationalInteger counter = new OperationalInteger();
            for (int i = count; i > 0; i--) {
                execute(source, identifier, pos, nbt, initialize, counter);
            }
            source.sendFeedback(TextUtil.format("commands.summon.success.counted", identifier).text(), true);
        } else {
            execute(source, identifier, pos, nbt, initialize, null);
            source.sendFeedback(TextUtil.format("commands.summon.success", identifier).text(), true);
        }
        return 1;
    }

    private static void execute(ServerCommandSource source, Identifier entity, Vec3d pos, NbtCompound nbt, boolean initialize, OperationalInteger counter) throws CommandSyntaxException {
        BlockPos blockPos = new BlockPos(pos);
        if (World.isValid(blockPos)) {
            NbtCompound nbtCompound = nbt.copy();
            nbtCompound.putString("id", entity.toString());
            ServerWorld serverWorld = source.getWorld();
            Entity entity2 = EntityType.loadEntityWithPassengers(nbtCompound, serverWorld, (entityx) -> {
                entityx.refreshPositionAndAngles(pos.x, pos.y, pos.z, entityx.getYaw(), entityx.getPitch());
                return entityx;
            });
            if (entity2 == null) {
                throw FAILED_EXCEPTION.create();
            } else {
                if (initialize && entity2 instanceof MobEntity mob) {
                    mob.initialize(source.getWorld(), source.getWorld().getLocalDifficulty(entity2.getBlockPos()), SpawnReason.COMMAND, null, null);
                }

                if (serverWorld.spawnNewEntityAndPassengers(entity2)) {
                    if (counter != null) {
                        counter.add();
                    }
                } else {
                    throw FAILED_UUID_EXCEPTION.create();
                }
            }
        } else {
            throw INVALID_POSITION_EXCEPTION.create();
        }
    }
}
