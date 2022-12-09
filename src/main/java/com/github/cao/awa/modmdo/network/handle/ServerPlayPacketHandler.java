package com.github.cao.awa.modmdo.network.handle;

import net.minecraft.network.*;
import net.minecraft.network.listener.*;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.text.*;

public abstract class ServerPlayPacketHandler implements ServerPlayPacketListener {
    @Override
    public void onHandSwing(HandSwingC2SPacket packet) {

    }

    @Override
    public void onGameMessage(ChatMessageC2SPacket packet) {

    }

    @Override
    public void onClientStatus(ClientStatusC2SPacket packet) {

    }

    @Override
    public void onClientSettings(ClientSettingsC2SPacket packet) {

    }

    @Override
    public void onButtonClick(ButtonClickC2SPacket packet) {

    }

    @Override
    public void onClickSlot(ClickSlotC2SPacket packet) {

    }

    @Override
    public void onCraftRequest(CraftRequestC2SPacket packet) {

    }

    @Override
    public void onCloseHandledScreen(CloseHandledScreenC2SPacket packet) {

    }

    @Override
    public void onCustomPayload(CustomPayloadC2SPacket packet) {

    }

    @Override
    public void onPlayerInteractEntity(PlayerInteractEntityC2SPacket packet) {

    }

    @Override
    public void onKeepAlive(KeepAliveC2SPacket packet) {

    }

    @Override
    public void onPlayerMove(PlayerMoveC2SPacket packet) {

    }

    @Override
    public void onPong(PlayPongC2SPacket packet) {

    }

    @Override
    public void onPlayerAbilities(UpdatePlayerAbilitiesC2SPacket packet) {

    }

    @Override
    public void onPlayerAction(PlayerActionC2SPacket packet) {

    }

    @Override
    public void onClientCommand(ClientCommandC2SPacket packet) {

    }

    @Override
    public void onPlayerInput(PlayerInputC2SPacket packet) {

    }

    @Override
    public void onUpdateSelectedSlot(UpdateSelectedSlotC2SPacket packet) {

    }

    @Override
    public void onCreativeInventoryAction(CreativeInventoryActionC2SPacket packet) {

    }

    @Override
    public void onSignUpdate(UpdateSignC2SPacket packet) {

    }

    @Override
    public void onPlayerInteractBlock(PlayerInteractBlockC2SPacket packet) {

    }

    @Override
    public void onPlayerInteractItem(PlayerInteractItemC2SPacket packet) {

    }

    @Override
    public void onSpectatorTeleport(SpectatorTeleportC2SPacket packet) {

    }

    @Override
    public void onResourcePackStatus(ResourcePackStatusC2SPacket packet) {

    }

    @Override
    public void onBoatPaddleState(BoatPaddleStateC2SPacket packet) {

    }

    @Override
    public void onVehicleMove(VehicleMoveC2SPacket packet) {

    }

    @Override
    public void onTeleportConfirm(TeleportConfirmC2SPacket packet) {

    }

    @Override
    public void onRecipeBookData(RecipeBookDataC2SPacket packet) {

    }

    @Override
    public void onRecipeCategoryOptions(RecipeCategoryOptionsC2SPacket packet) {

    }

    @Override
    public void onAdvancementTab(AdvancementTabC2SPacket packet) {

    }

    @Override
    public void onRequestCommandCompletions(RequestCommandCompletionsC2SPacket packet) {

    }

    @Override
    public void onUpdateCommandBlock(UpdateCommandBlockC2SPacket packet) {

    }

    @Override
    public void onUpdateCommandBlockMinecart(UpdateCommandBlockMinecartC2SPacket packet) {

    }

    @Override
    public void onPickFromInventory(PickFromInventoryC2SPacket packet) {

    }

    @Override
    public void onRenameItem(RenameItemC2SPacket packet) {

    }

    @Override
    public void onUpdateBeacon(UpdateBeaconC2SPacket packet) {

    }

    @Override
    public void onStructureBlockUpdate(UpdateStructureBlockC2SPacket packet) {

    }

    @Override
    public void onMerchantTradeSelect(SelectMerchantTradeC2SPacket packet) {

    }

    @Override
    public void onBookUpdate(BookUpdateC2SPacket packet) {

    }

    @Override
    public void onQueryEntityNbt(QueryEntityNbtC2SPacket packet) {

    }

    @Override
    public void onQueryBlockNbt(QueryBlockNbtC2SPacket packet) {

    }

    @Override
    public void onJigsawUpdate(UpdateJigsawC2SPacket packet) {

    }

    @Override
    public void onJigsawGenerating(JigsawGeneratingC2SPacket packet) {

    }

    @Override
    public void onUpdateDifficulty(UpdateDifficultyC2SPacket packet) {

    }

    @Override
    public void onUpdateDifficultyLock(UpdateDifficultyLockC2SPacket packet) {

    }

    @Override
    public void onDisconnected(Text reason) {

    }

    @Override
    public ClientConnection getConnection() {
        return null;
    }
}
