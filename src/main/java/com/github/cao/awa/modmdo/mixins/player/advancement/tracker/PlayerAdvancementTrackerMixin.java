package com.github.cao.awa.modmdo.mixins.player.advancement.tracker;

import com.google.common.io.*;
import com.google.gson.*;
import com.google.gson.internal.*;
import com.google.gson.reflect.*;
import com.google.gson.stream.*;
import com.mojang.brigadier.*;
import com.mojang.datafixers.*;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.*;
import net.minecraft.advancement.*;
import net.minecraft.datafixer.*;
import net.minecraft.network.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.nio.charset.*;
import java.util.*;
import java.util.concurrent.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Mixin(PlayerAdvancementTracker.class)
public abstract class PlayerAdvancementTrackerMixin {
    @Shadow
    @Final
    private static TypeToken<Map<Identifier, AdvancementProgress>> JSON_TYPE;
    @Shadow
    @Final
    private static Gson GSON;
    @Shadow
    @Final
    private Map<Advancement, AdvancementProgress> advancementToProgress;
    @Shadow
    @Final
    private File advancementFile;
    @Shadow
    @Final
    private DataFixer dataFixer;
    @Shadow
    private ServerPlayerEntity owner;
    @Shadow
    @Final
    private Set<Advancement> progressUpdates;
    @Shadow
    @Final
    private PlayerManager playerManager;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void initAsync(DataFixer dataFixer, PlayerManager playerManager, ServerAdvancementLoader advancementLoader, File advancementFile, ServerPlayerEntity owner, CallbackInfo ci) {
        ((PlayerAdvancementTrackerInterface)this).setAdvancementToProgress(Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>()));
        ((PlayerAdvancementTrackerInterface)this).setVisibleAdvancements(ObjectSets.synchronize(new ObjectOpenHashSet<>()));
        ((PlayerAdvancementTrackerInterface)this).setVisibilityUpdates(ObjectSets.synchronize(new ObjectOpenHashSet<>()));
        ((PlayerAdvancementTrackerInterface)this).setProgressUpdates(ObjectSets.synchronize(new ObjectOpenHashSet<>()));
    }

    @Inject(method = "load", at = @At("HEAD"))
    public void noBlockAsyncLoad(ServerAdvancementLoader advancementLoader, CallbackInfo ci) {
        TRACKER.info("Async loading advancement");
        CompletableFuture.runAsync(() -> optimizedLoad(advancementLoader));
    }

    private void optimizedLoad(ServerAdvancementLoader advancementLoader) {
        if (this.advancementFile.isFile()) {
            try {
                JsonReader jsonReader = new JsonReader(new StringReader(Files.toString(this.advancementFile, StandardCharsets.UTF_8)));

                try {
                    jsonReader.setLenient(false);
                    com.mojang.serialization.Dynamic<JsonElement> dynamic = new Dynamic<>(JsonOps.INSTANCE, Streams.parse(jsonReader));
                    if (dynamic.get("DataVersion").asNumber().result().isEmpty()) {
                        dynamic = dynamic.set("DataVersion", dynamic.createInt(1343));
                    }

                    dynamic = this.dataFixer.update(DataFixTypes.ADVANCEMENTS.getTypeReference(), dynamic, dynamic.get("DataVersion").asInt(0), SharedConstants.getGameVersion().getWorldVersion());
                    dynamic = dynamic.remove("DataVersion");
                    Map<Identifier, AdvancementProgress> map = GSON.getAdapter(JSON_TYPE).fromJsonTree(dynamic.getValue());
                    if (map == null) {
                        throw new JsonParseException("Found null for advancements");
                    }

                    map.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEach(entry -> {
                        Advancement advancement = advancementLoader.get(entry.getKey());
                        if (advancement == null) {
                            TRACKER.warn("Ignored advancement '" + entry.getKey() + "' in progress file " + this.advancementFile + " - it doesn't exist anymore?");
                        } else {
                            this.synchronizedInitProgress(advancement, entry.getValue());
                        }
                    });
                } catch (Throwable var10) {
                    try {
                        jsonReader.close();
                    } catch (Throwable var9) {
                        var10.addSuppressed(var9);
                    }

                    throw var10;
                }

                jsonReader.close();
            } catch (JsonParseException var11) {
                TRACKER.err("Couldn't parse player advancements in " + this.advancementFile, var11);
            } catch (IOException var12) {
                TRACKER.err("Couldn't access player advancements in " + this.advancementFile, var12);
            }
        }

        this.rewardEmptyAdvancements(advancementLoader);
        this.updateCompleted();
        this.beginTrackingAllAdvancements(advancementLoader);
    }

    @Shadow
    protected abstract void updateCompleted();

    @Shadow
    protected abstract void beginTrackingAllAdvancements(ServerAdvancementLoader advancementLoader);

    private void synchronizedInitProgress(Advancement advancement, AdvancementProgress progress) {
        progress.init(advancement.getCriteria(), advancement.getRequirements());
        this.advancementToProgress.put(advancement, progress);
    }

    private void rewardEmptyAdvancements(ServerAdvancementLoader advancementLoader) {
        for (Advancement advancement : advancementLoader.getAdvancements()) {
            if (advancement.getCriteria().isEmpty()) {
                this.grantCriterion(advancement, "");
                advancement.getRewards().apply(this.owner);
            }
        }
    }

    public boolean grantCriterion(Advancement advancement, String criterionName) {
        boolean bl = false;
        AdvancementProgress advancementProgress = this.getProgress(advancement);
        boolean bl2 = advancementProgress.isDone();
        if (advancementProgress.obtain(criterionName)) {
            this.endTrackingCompleted(advancement);
            this.progressUpdates.add(advancement);
            bl = true;
            if (! bl2 && advancementProgress.isDone()) {
                advancement.getRewards().apply(this.owner);
                if (advancement.getDisplay() != null && advancement.getDisplay().shouldAnnounceToChat() && this.owner.world.getGameRules().getBoolean(GameRules.ANNOUNCE_ADVANCEMENTS)) {
                    this.playerManager.broadcast(new TranslatableText("chat.type.advancement." + advancement.getDisplay().getFrame().getId(), this.owner.getDisplayName(), advancement.toHoverableText()), MessageType.SYSTEM, Util.NIL_UUID);
                }
            }
        }

        if (advancementProgress.isDone()) {
            this.updateDisplay(advancement);
        }

        return bl;
    }

    @Shadow
    public abstract AdvancementProgress getProgress(Advancement advancement);

    @Shadow
    protected abstract void endTrackingCompleted(Advancement advancement);

    @Shadow
    protected abstract void updateDisplay(Advancement advancement);
}
