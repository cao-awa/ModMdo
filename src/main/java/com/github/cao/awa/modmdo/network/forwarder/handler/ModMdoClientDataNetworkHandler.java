package com.github.cao.awa.modmdo.network.forwarder.handler;

import com.github.cao.awa.modmdo.network.forwarder.process.*;
import com.github.cao.awa.modmdo.storage.*;
import net.minecraft.network.*;
import net.minecraft.network.listener.*;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.server.*;
import net.minecraft.text.*;
import org.jetbrains.annotations.*;

import java.net.*;

public class ModMdoClientDataNetworkHandler implements ClientPlayPacketListener {
    private final MinecraftServer server;
    private final ModMdoDataProcessor processor;

    public ModMdoClientDataNetworkHandler(MinecraftServer server, InetSocketAddress address, @Nullable ClientConnection connection) {
        this.server = server;
        if (connection != null) {
            connection.setPacketListener(this);
        }
        processor = new ModMdoDataProcessor(server, address, connection, NetworkSide.CLIENTBOUND);
        SharedVariables.modmdoConnections.add(processor);
    }

    @Override
    public void onDisconnected(Text reason) {

    }

    @Override
    public ClientConnection getConnection() {
        return processor.getConnection();
    }

    @Override
    public void onEntitySpawn(EntitySpawnS2CPacket packet) {

    }

    @Override
    public void onExperienceOrbSpawn(ExperienceOrbSpawnS2CPacket packet) {

    }

    @Override
    public void onVibration(VibrationS2CPacket packet) {

    }

    @Override
    public void onMobSpawn(MobSpawnS2CPacket packet) {

    }

    @Override
    public void onScoreboardObjectiveUpdate(ScoreboardObjectiveUpdateS2CPacket packet) {

    }

    @Override
    public void onPaintingSpawn(PaintingSpawnS2CPacket packet) {

    }

    @Override
    public void onPlayerSpawn(PlayerSpawnS2CPacket packet) {

    }

    @Override
    public void onEntityAnimation(EntityAnimationS2CPacket packet) {

    }

    @Override
    public void onStatistics(StatisticsS2CPacket packet) {

    }

    @Override
    public void onUnlockRecipes(UnlockRecipesS2CPacket packet) {

    }

    @Override
    public void onBlockBreakingProgress(BlockBreakingProgressS2CPacket packet) {

    }

    @Override
    public void onSignEditorOpen(SignEditorOpenS2CPacket packet) {

    }

    @Override
    public void onBlockEntityUpdate(BlockEntityUpdateS2CPacket packet) {

    }

    @Override
    public void onBlockEvent(BlockEventS2CPacket packet) {

    }

    @Override
    public void onBlockUpdate(BlockUpdateS2CPacket packet) {

    }

    @Override
    public void onGameMessage(GameMessageS2CPacket packet) {

    }

    @Override
    public void onChunkDeltaUpdate(ChunkDeltaUpdateS2CPacket packet) {

    }

    @Override
    public void onMapUpdate(MapUpdateS2CPacket packet) {

    }

    @Override
    public void onCloseScreen(CloseScreenS2CPacket packet) {

    }

    @Override
    public void onInventory(InventoryS2CPacket packet) {

    }

    @Override
    public void onOpenHorseScreen(OpenHorseScreenS2CPacket packet) {

    }

    @Override
    public void onScreenHandlerPropertyUpdate(ScreenHandlerPropertyUpdateS2CPacket packet) {

    }

    @Override
    public void onScreenHandlerSlotUpdate(ScreenHandlerSlotUpdateS2CPacket packet) {

    }

    @Override
    public void onCustomPayload(CustomPayloadS2CPacket packet) {
        processor.process(packet);
    }

    @Override
    public void onDisconnect(DisconnectS2CPacket packet) {

    }

    @Override
    public void onEntityStatus(EntityStatusS2CPacket packet) {

    }

    @Override
    public void onEntityAttach(EntityAttachS2CPacket packet) {

    }

    @Override
    public void onEntityPassengersSet(EntityPassengersSetS2CPacket packet) {

    }

    @Override
    public void onExplosion(ExplosionS2CPacket packet) {

    }

    @Override
    public void onGameStateChange(GameStateChangeS2CPacket packet) {

    }

    @Override
    public void onKeepAlive(KeepAliveS2CPacket packet) {

    }

    @Override
    public void onChunkData(ChunkDataS2CPacket packet) {

    }

    @Override
    public void onUnloadChunk(UnloadChunkS2CPacket packet) {

    }

    @Override
    public void onWorldEvent(WorldEventS2CPacket packet) {

    }

    @Override
    public void onGameJoin(GameJoinS2CPacket packet) {

    }

    @Override
    public void onEntity(EntityS2CPacket packet) {

    }

    @Override
    public void onPlayerPositionLook(PlayerPositionLookS2CPacket packet) {

    }

    @Override
    public void onParticle(ParticleS2CPacket packet) {

    }

    @Override
    public void onPing(PlayPingS2CPacket packet) {

    }

    @Override
    public void onPlayerAbilities(PlayerAbilitiesS2CPacket packet) {

    }

    @Override
    public void onPlayerList(PlayerListS2CPacket packet) {

    }

    @Override
    public void onEntitiesDestroy(EntitiesDestroyS2CPacket packet) {

    }

    @Override
    public void onRemoveEntityStatusEffect(RemoveEntityStatusEffectS2CPacket packet) {

    }

    @Override
    public void onPlayerRespawn(PlayerRespawnS2CPacket packet) {

    }

    @Override
    public void onEntitySetHeadYaw(EntitySetHeadYawS2CPacket packet) {

    }

    @Override
    public void onUpdateSelectedSlot(UpdateSelectedSlotS2CPacket packet) {

    }

    @Override
    public void onScoreboardDisplay(ScoreboardDisplayS2CPacket packet) {

    }

    @Override
    public void onEntityTrackerUpdate(EntityTrackerUpdateS2CPacket packet) {

    }

    @Override
    public void onEntityVelocityUpdate(EntityVelocityUpdateS2CPacket packet) {

    }

    @Override
    public void onEntityEquipmentUpdate(EntityEquipmentUpdateS2CPacket packet) {

    }

    @Override
    public void onExperienceBarUpdate(ExperienceBarUpdateS2CPacket packet) {

    }

    @Override
    public void onHealthUpdate(HealthUpdateS2CPacket packet) {

    }

    @Override
    public void onTeam(TeamS2CPacket packet) {

    }

    @Override
    public void onScoreboardPlayerUpdate(ScoreboardPlayerUpdateS2CPacket packet) {

    }

    @Override
    public void onPlayerSpawnPosition(PlayerSpawnPositionS2CPacket packet) {

    }

    @Override
    public void onWorldTimeUpdate(WorldTimeUpdateS2CPacket packet) {

    }

    @Override
    public void onPlaySound(PlaySoundS2CPacket packet) {

    }

    @Override
    public void onPlaySoundFromEntity(PlaySoundFromEntityS2CPacket packet) {

    }

    @Override
    public void onPlaySoundId(PlaySoundIdS2CPacket packet) {

    }

    @Override
    public void onItemPickupAnimation(ItemPickupAnimationS2CPacket packet) {

    }

    @Override
    public void onEntityPosition(EntityPositionS2CPacket packet) {

    }

    @Override
    public void onEntityAttributes(EntityAttributesS2CPacket packet) {

    }

    @Override
    public void onEntityStatusEffect(EntityStatusEffectS2CPacket packet) {

    }

    @Override
    public void onSynchronizeTags(SynchronizeTagsS2CPacket packet) {

    }

    @Override
    public void onEndCombat(EndCombatS2CPacket packet) {

    }

    @Override
    public void onEnterCombat(EnterCombatS2CPacket packet) {

    }

    @Override
    public void onDeathMessage(DeathMessageS2CPacket packet) {

    }

    @Override
    public void onDifficulty(DifficultyS2CPacket packet) {

    }

    @Override
    public void onSetCameraEntity(SetCameraEntityS2CPacket packet) {

    }

    @Override
    public void onWorldBorderInitialize(WorldBorderInitializeS2CPacket packet) {

    }

    @Override
    public void onWorldBorderInterpolateSize(WorldBorderInterpolateSizeS2CPacket packet) {

    }

    @Override
    public void onWorldBorderSizeChanged(WorldBorderSizeChangedS2CPacket packet) {

    }

    @Override
    public void onWorldBorderWarningTimeChanged(WorldBorderWarningTimeChangedS2CPacket packet) {

    }

    @Override
    public void onWorldBorderWarningBlocksChanged(WorldBorderWarningBlocksChangedS2CPacket packet) {

    }

    @Override
    public void onWorldBorderCenterChanged(WorldBorderCenterChangedS2CPacket packet) {

    }

    @Override
    public void onPlayerListHeader(PlayerListHeaderS2CPacket packet) {

    }

    @Override
    public void onResourcePackSend(ResourcePackSendS2CPacket packet) {

    }

    @Override
    public void onBossBar(BossBarS2CPacket packet) {

    }

    @Override
    public void onCooldownUpdate(CooldownUpdateS2CPacket packet) {

    }

    @Override
    public void onVehicleMove(VehicleMoveS2CPacket packet) {

    }

    @Override
    public void onAdvancements(AdvancementUpdateS2CPacket packet) {

    }

    @Override
    public void onSelectAdvancementTab(SelectAdvancementTabS2CPacket packet) {

    }

    @Override
    public void onCraftFailedResponse(CraftFailedResponseS2CPacket packet) {

    }

    @Override
    public void onCommandTree(CommandTreeS2CPacket packet) {

    }

    @Override
    public void onStopSound(StopSoundS2CPacket packet) {

    }

    @Override
    public void onCommandSuggestions(CommandSuggestionsS2CPacket packet) {

    }

    @Override
    public void onSynchronizeRecipes(SynchronizeRecipesS2CPacket packet) {

    }

    @Override
    public void onLookAt(LookAtS2CPacket packet) {

    }

    @Override
    public void onNbtQueryResponse(NbtQueryResponseS2CPacket packet) {

    }

    @Override
    public void onLightUpdate(LightUpdateS2CPacket packet) {

    }

    @Override
    public void onOpenWrittenBook(OpenWrittenBookS2CPacket packet) {

    }

    @Override
    public void onOpenScreen(OpenScreenS2CPacket packet) {

    }

    @Override
    public void onSetTradeOffers(SetTradeOffersS2CPacket packet) {

    }

    @Override
    public void onChunkLoadDistance(ChunkLoadDistanceS2CPacket packet) {

    }

    @Override
    public void onSimulationDistance(SimulationDistanceS2CPacket packet) {

    }

    @Override
    public void onChunkRenderDistanceCenter(ChunkRenderDistanceCenterS2CPacket packet) {

    }

    @Override
    public void onPlayerActionResponse(PlayerActionResponseS2CPacket packet) {

    }

    @Override
    public void onOverlayMessage(OverlayMessageS2CPacket packet) {

    }

    @Override
    public void onSubtitle(SubtitleS2CPacket packet) {

    }

    @Override
    public void onTitle(TitleS2CPacket packet) {

    }

    @Override
    public void onTitleFade(TitleFadeS2CPacket packet) {

    }

    @Override
    public void onTitleClear(ClearTitleS2CPacket packet) {

    }
}
