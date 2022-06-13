package com.github.cao.awa.modmdo.event.trigger;

import com.github.cao.awa.modmdo.event.*;
import com.github.cao.awa.modmdo.event.entity.*;
import com.github.cao.awa.modmdo.event.trigger.connection.*;
import com.github.cao.awa.modmdo.event.trigger.kill.*;
import com.github.cao.awa.modmdo.event.trigger.message.*;
import com.github.cao.awa.modmdo.event.trigger.motd.*;
import com.github.cao.awa.modmdo.event.trigger.persistent.*;
import com.github.cao.awa.modmdo.event.trigger.selector.*;
import com.github.cao.awa.modmdo.event.trigger.summon.*;
import com.github.cao.awa.modmdo.event.trigger.teleport.*;
import com.github.cao.awa.modmdo.event.trigger.trace.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.operational.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.entity.*;
import net.minecraft.entity.boss.*;
import net.minecraft.entity.boss.dragon.*;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.projectile.*;
import net.minecraft.entity.projectile.thrown.*;
import net.minecraft.entity.vehicle.*;
import net.minecraft.server.network.*;
import org.json.*;

import java.io.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

public class ModMdoTriggerBuilder {
    public static final Object2ObjectOpenHashMap<String, String> classMap = EntrustParser.operation(new Object2ObjectOpenHashMap<>(), map -> {
        map.put("net.minecraft.server.network.ServerPlayerEntity", ServerPlayerEntity.class.getName());
        map.put("net.minecraft.entity.passive.PigEntity", PigEntity.class.getName());
        map.put("net.minecraft.entity.passive.AxolotlEntity", AxolotlEntity.class.getName());
        map.put("net.minecraft.entity.passive.BatEntity", BatEntity.class.getName());
        map.put("net.minecraft.entity.passive.BeeEntity", BeeEntity.class.getName());
        map.put("net.minecraft.entity.passive.CatEntity", CatEntity.class.getName());
        map.put("net.minecraft.entity.passive.ChickenEntity", ChickenEntity.class.getName());
        map.put("net.minecraft.entity.passive.CodEntity", CodEntity.class.getName());
        map.put("net.minecraft.entity.passive.CowEntity", CowEntity.class.getName());
        map.put("net.minecraft.entity.passive.DolphinEntity", DolphinEntity.class.getName());
        map.put("net.minecraft.entity.passive.DonkeyEntity", DonkeyEntity.class.getName());
        map.put("net.minecraft.entity.passive.FoxEntity", FoxEntity.class.getName());
        map.put("net.minecraft.entity.passive.GlowSquidEntity", GlowSquidEntity.class.getName());
        map.put("net.minecraft.entity.passive.GoatEntity", GoatEntity.class.getName());
        map.put("net.minecraft.entity.passive.HorseEntity", HorseEntity.class.getName());
        map.put("net.minecraft.entity.passive.IronGolemEntity", IronGolemEntity.class.getName());
        map.put("net.minecraft.entity.passive.LlamaEntity", LlamaEntity.class.getName());
        map.put("net.minecraft.entity.passive.MooshroomEntity", MooshroomEntity.class.getName());
        map.put("net.minecraft.entity.passive.MuleEntity", MuleEntity.class.getName());
        map.put("net.minecraft.entity.passive.OcelotEntity", OcelotEntity.class.getName());
        map.put("net.minecraft.entity.passive.PandaEntity", PandaEntity.class.getName());
        map.put("net.minecraft.entity.passive.ParrotEntity", ParrotEntity.class.getName());
        map.put("net.minecraft.entity.passive.PolarBearEntity", PolarBearEntity.class.getName());
        map.put("net.minecraft.entity.passive.PufferfishEntity", PufferfishEntity.class.getName());
        map.put("net.minecraft.entity.passive.RabbitEntity", RabbitEntity.class.getName());
        map.put("net.minecraft.entity.passive.SalmonEntity", SalmonEntity.class.getName());
        map.put("net.minecraft.entity.passive.SheepEntity", SheepEntity.class.getName());
        map.put("net.minecraft.entity.passive.SnowGolemEntity", SnowGolemEntity.class.getName());
        map.put("net.minecraft.entity.passive.SquidEntity", SquidEntity.class.getName());
        map.put("net.minecraft.entity.passive.StriderEntity", StriderEntity.class.getName());
        map.put("net.minecraft.entity.passive.TraderLlamaEntity", TraderLlamaEntity.class.getName());
        map.put("net.minecraft.entity.passive.TropicalFishEntity", TropicalFishEntity.class.getName());
        map.put("net.minecraft.entity.passive.TurtleEntity", TurtleEntity.class.getName());
        map.put("net.minecraft.entity.passive.VillagerEntity", VillagerEntity.class.getName());
        map.put("net.minecraft.entity.passive.ZombifiedPiglinEntity", ZombifiedPiglinEntity.class.getName());
        map.put("net.minecraft.entity.vehicle.WanderingTraderEntity", WanderingTraderEntity.class.getName());
        map.put("net.minecraft.entity.vehicle.BoatEntity", BoatEntity.class.getName());
        map.put("net.minecraft.entity.vehicle.ChestMinecartEntity", ChestMinecartEntity.class.getName());
        map.put("net.minecraft.entity.vehicle.CommandBlockMinecartEntity", CommandBlockMinecartEntity.class.getName());
        map.put("net.minecraft.entity.vehicle.FurnaceMinecartEntity", FurnaceMinecartEntity.class.getName());
        map.put("net.minecraft.entity.vehicle.HopperMinecartEntity", HopperMinecartEntity.class.getName());
        map.put("net.minecraft.entity.vehicle.MinecartEntity", MinecartEntity.class.getName());
        map.put("net.minecraft.entity.vehicle.SpawnerMinecartEntity", SpawnerMinecartEntity.class.getName());
        map.put("net.minecraft.entity.vehicle.TntMinecartEntity", TntMinecartEntity.class.getName());
        map.put("net.minecraft.entity.boss.WitherEntity", WitherEntity.class.getName());
        map.put("net.minecraft.entity.boss.dragon.EnderDragonEntity", EnderDragonEntity.class.getName());
        map.put("net.minecraft.entity.mob.BlazeEntity", BlazeEntity.class.getName());
        map.put("net.minecraft.entity.mob.CaveSpiderEntity", CaveSpiderEntity.class.getName());
        map.put("net.minecraft.entity.mob.CreeperEntity", CreeperEntity.class.getName());
        map.put("net.minecraft.entity.mob.DrownedEntity", DrownedEntity.class.getName());
        map.put("net.minecraft.entity.mob.ElderGuardianEntity", ElderGuardianEntity.class.getName());
        map.put("net.minecraft.entity.mob.EndermanEntity", EndermanEntity.class.getName());
        map.put("net.minecraft.entity.mob.EvokerEntity", EvokerEntity.class.getName());
        map.put("net.minecraft.entity.mob.EvokerFangsEntity", EvokerFangsEntity.class.getName());
        map.put("net.minecraft.entity.mob.GhastEntity", GhastEntity.class.getName());
        map.put("net.minecraft.entity.mob.GiantEntity", GiantEntity.class.getName());
        map.put("net.minecraft.entity.mob.GuardianEntity", GuardianEntity.class.getName());
        map.put("net.minecraft.entity.mob.HoglinEntity", HoglinEntity.class.getName());
        map.put("net.minecraft.entity.mob.HuskEntity", HuskEntity.class.getName());
        map.put("net.minecraft.entity.mob.IllusionerEntity", IllusionerEntity.class.getName());
        map.put("net.minecraft.entity.mob.MagmaCubeEntity", MagmaCubeEntity.class.getName());
        map.put("net.minecraft.entity.mob.PhantomEntity", PhantomEntity.class.getName());
        map.put("net.minecraft.entity.mob.PiglinBruteEntity", PiglinBruteEntity.class.getName());
        map.put("net.minecraft.entity.mob.PiglinEntity", PiglinEntity.class.getName());
        map.put("net.minecraft.entity.mob.PillagerEntity", PillagerEntity.class.getName());
        map.put("net.minecraft.entity.mob.WolfEntity", WolfEntity.class.getName());
        map.put("net.minecraft.entity.mob.RavagerEntity", RavagerEntity.class.getName());
        map.put("net.minecraft.entity.mob.ShulkerEntity", ShulkerEntity.class.getName());
        map.put("net.minecraft.entity.mob.SilverfishEntity", SilverfishEntity.class.getName());
        map.put("net.minecraft.entity.mob.SkeletonEntity", SkeletonEntity.class.getName());
        map.put("net.minecraft.entity.mob.SkeletonHorseEntity", SkeletonHorseEntity.class.getName());
        map.put("net.minecraft.entity.mob.SpiderEntity", SpiderEntity.class.getName());
        map.put("net.minecraft.entity.mob.StrayEntity", StrayEntity.class.getName());
        map.put("net.minecraft.entity.mob.VexEntity", VexEntity.class.getName());
        map.put("net.minecraft.entity.mob.VindicatorEntity", VindicatorEntity.class.getName());
        map.put("net.minecraft.entity.mob.WitchEntity", WitchEntity.class.getName());
        map.put("net.minecraft.entity.mob.WitherSkeletonEntity", WitherSkeletonEntity.class.getName());
        map.put("net.minecraft.entity.mob.ZoglinEntity", ZoglinEntity.class.getName());
        map.put("net.minecraft.entity.mob.ZombieEntity", ZombieEntity.class.getName());
        map.put("net.minecraft.entity.mob.ZombieHorseEntity", ZombieHorseEntity.class.getName());
        map.put("net.minecraft.entity.mob.ZombieVillagerEntity", ZombieVillagerEntity.class.getName());
        map.put("net.minecraft.entity.projectile.ArrowEntity", ArrowEntity.class.getName());
        map.put("net.minecraft.entity.projectile.DragonFireballEntity", DragonFireballEntity.class.getName());
        map.put("net.minecraft.entity.projectile.FireballEntity", FireballEntity.class.getName());
        map.put("net.minecraft.entity.projectile.FireworkRocketEntity", FireworkRocketEntity.class.getName());
        map.put("net.minecraft.entity.projectile.FishingBobberEntity", FishingBobberEntity.class.getName());
        map.put("net.minecraft.entity.projectile.LlamaSpitEntity", LlamaSpitEntity.class.getName());
        map.put("net.minecraft.entity.projectile.ShulkerBulletEntity", ShulkerBulletEntity.class.getName());
        map.put("net.minecraft.entity.projectile.SmallFireballEntity", SmallFireballEntity.class.getName());
        map.put("net.minecraft.entity.projectile.SpectralArrowEntity", SpectralArrowEntity.class.getName());
        map.put("net.minecraft.entity.projectile.TridentEntity", TridentEntity.class.getName());
        map.put("net.minecraft.entity.projectile.WitherSkullEntity", WitherSkullEntity.class.getName());
        map.put("net.minecraft.entity.projectile.thrown.EggEntity", EggEntity.class.getName());
        map.put("net.minecraft.entity.projectile.thrown.EnderPearlEntity", EnderPearlEntity.class.getName());
        map.put("net.minecraft.entity.projectile.thrown.ExperienceBottleEntity", ExperienceBottleEntity.class.getName());
        map.put("net.minecraft.entity.projectile.thrown.PotionEntity", PotionEntity.class.getName());
        map.put("net.minecraft.entity.projectile.thrown.SnowballEntity", SnowballEntity.class.getName());
        map.put("net.minecraft.entity.ItemEntity", ItemEntity.class.getName());
        map.put("net.minecraft.entity.TntEntity", TntEntity.class.getName());
        map.put("net.minecraft.entity.EyeOfEnderEntity", EyeOfEnderEntity.class.getName());
        map.put("net.minecraft.entity.ExperienceOrbEntity", ExperienceOrbEntity.class.getName());
        map.put("net.minecraft.entity.FallingBlockEntity", FallingBlockEntity.class.getName());
        map.put("net.minecraft.entity.LightningEntity", LightningEntity.class.getName());
    });
    public final ObjectArrayList<String> events = EntrustParser.operation(new ObjectArrayList<>(), list -> {
        list.add(DisconnectTrigger.class.getName());
        list.add(KillEntityTrigger.class.getName());
        list.add(SendMessageTrigger.class.getName());
        list.add(MotdModifyTrigger.class.getName());
        list.add(PersistentModifyTrigger.class.getName());
        list.add(SummonTrigger.class.getName());
        list.add(TeleportEntityTrigger.class.getName());
    });
    public final ObjectArrayList<String> targeted = EntrustParser.operation(new ObjectArrayList<>(), list -> {
        list.add(DisconnectTrigger.class.getName());
        list.add(KillEntityTrigger.class.getName());
        list.add(SendMessageTrigger.class.getName());
        list.add(TeleportEntityTrigger.class.getName());
    });

    public void register(JSONObject json, File trace) {
        JSONObject e = json.getJSONObject("event");
        String name = e.getString("instanceof");
        try {
            ModMdoEvent<?> register = event.targeted.get(name);
            if (register == null || !targeted.contains(name)) {
                register = event.events.get(name);
                register.register(event -> prepare(e, event, trace), trace);
            } else {
                register.register(event -> prepareTargeted(e, (EntityTargetedEvent<?>) event, trace), trace);
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException("Event \"" + name + "\" not found, may you got key it wrong? will be not register this event", ex);
        }
    }

    public void prepareTargeted(JSONObject event, EntityTargetedEvent<?> targeted, File trace) {
        String instance = EntrustParser.trying(() -> event.getString("target-instanceof"));
        if (targeted.getTargeted().size() > 1 || EntrustParser.trying(() -> classMap.getOrDefault(instance, instance).equals(targeted.getTargeted().get(0).getClass().getName()), () -> true)) {
            TriggerSelector selector = event.has("controller") ? controller(event.getJSONObject("controller")) : new AllSelector();
            OperationalInteger i = new OperationalInteger();
            selector.select(event.getJSONObject("triggers"), (name, json) -> {
                Temporary action = () -> {
                    EntrustExecution.notNull(EntrustParser.trying(() -> {
                        TargetedTrigger<EntityTargetedEvent<?>> trigger = (TargetedTrigger<EntityTargetedEvent<?>>) Class.forName(json.getString("instanceof")).getDeclaredConstructor().newInstance();
                        return trigger.build(targeted, json, new TriggerTrace(trace, i.get(), name));
                    }, ex -> {
                        ex.printStackTrace();
                        TRACKER.submit(Thread.currentThread(), "Failed build event: " + new TriggerTrace(trace, i.get(), name).at(), ex);
                        return null;
                    }), ModMdoEventTrigger::action);
                    i.add();
                };

                EntrustExecution.tryTemporary(() -> {
                    JSONObject awaiting = json.getJSONObject("await");
                    int wait = awaiting.getInt("or-wait");
                    SharedVariables.event.events.get(awaiting.getString("instanceof")).await(action, wait, trace);
                }, ex -> action.apply());
            });
        }
    }

    public void prepare(JSONObject event, ModMdoEvent<?> targeted, File trace) {
        TriggerSelector selector = event.has("controller") ? controller(event.getJSONObject("controller")) : new AllSelector();
        OperationalInteger i = new OperationalInteger();
        selector.select(event.getJSONObject("triggers"), (name, json) -> {
            Temporary action = () -> {
                EntrustExecution.notNull(EntrustParser.trying(() -> {
                    ModMdoEventTrigger<ModMdoEvent<?>> trigger = (ModMdoEventTrigger<ModMdoEvent<?>>) Class.forName(json.getString("instanceof")).getDeclaredConstructor().newInstance();
                    return trigger.build(targeted, json, new TriggerTrace(trace, i.get(), name));
                }, ex -> {
                    TRACKER.submit("Failed build event: " + new TriggerTrace(trace, i.get(), name).at(), ex);
                    return null;
                }), ModMdoEventTrigger::action);
                i.add();
            };

            EntrustExecution.tryTemporary(() -> {
                JSONObject awaiting = json.getJSONObject("await");
                int wait = awaiting.getInt("or-wait");
                SharedVariables.event.events.get(awaiting.getString("instanceof")).await(action, wait, trace);
            }, ex -> action.apply());
        });
    }

    public TriggerSelector controller(JSONObject json) {
        return EntrustParser.trying(() -> {
            TriggerSelector selector = (TriggerSelector) Class.forName(json.getString("instanceof")).getDeclaredConstructor().newInstance();
            selector.build(json);
            return selector;
        }, AllSelector::new);
    }
}
