package com.github.zhuaidadaya.modmdo.event;

import com.github.zhuaidadaya.modmdo.event.block.destroy.*;
import com.github.zhuaidadaya.modmdo.event.block.place.*;
import com.github.zhuaidadaya.modmdo.event.entity.damage.*;
import com.github.zhuaidadaya.modmdo.event.entity.player.death.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.server.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraft.world.explosion.*;

public class ModMdoEventTracer {
    public final EntityDeathEvent entityDeath = EntityDeathEvent.snap();
    public final BlockDestroyEvent blockDestroy = BlockDestroyEvent.snap();
    public final BlockPlaceEvent blockPlace = BlockPlaceEvent.snap();
    public final BlockExplosionDestroyEvent blockExplosion = BlockExplosionDestroyEvent.snap();
    public final EntityDamageEvent entityDamage = EntityDamageEvent.snap();

    public void submitBlockDestroy(PlayerEntity player, BlockState state, BlockPos pos, World world, MinecraftServer server) {
        if (blockDestroy.isSubmitted()) {
            blockDestroy.action();
        } else {
            blockDestroy.submit(new BlockDestroyEvent(player, state, pos, world, server));
        }
    }

    public void submitBlockPlace(LivingEntity player, BlockState state, BlockPos pos, World world, ItemStack itemStack, MinecraftServer server) {
        if (blockPlace.isSubmitted()) {
            blockPlace.action();
        } else {
            blockPlace.submit(new BlockPlaceEvent(player, state, pos, world, itemStack, server));
        }
    }

    public void submitBlockExplosion(Explosion explosion, BlockState state, BlockPos pos, World world, MinecraftServer server) {
        blockExplosion.immediately(new BlockExplosionDestroyEvent(explosion, state, pos, world, server));
    }

    public void submitEntityDeath(LivingEntity entity, LivingEntity perpetrator, Vec3d pos, MinecraftServer server) {
        entityDeath.immediately(new EntityDeathEvent(entity, perpetrator, pos, server));
    }

    public void submitEntityDamage(LivingEntity entity, DamageSource damageSource, float originalHealth, float damage, World world, MinecraftServer server){
        entityDamage.immediately(new EntityDamageEvent(entity, damageSource, originalHealth, damage, world, server));
    }
}
