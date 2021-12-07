package com.github.zhuaidadaya.modMdo.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(InGameHud.class)
public abstract class TestMixin extends DrawableHelper {

    @Shadow
    @Final
    private static Identifier WIDGETS_TEXTURE;
    @Shadow
    private int scaledWidth;
    @Shadow
    @Final
    private MinecraftClient client;
    @Shadow
    private int scaledHeight;

    @Shadow
    protected abstract PlayerEntity getCameraPlayer();

    @Shadow
    protected abstract void renderHotbarItem(int x, int y, float tickDelta, PlayerEntity player, ItemStack stack, int seed);

    /**
     * @author
     * @reason
     */
    @Overwrite
    private void renderHotbar(float tickDelta, MatrixStack matrices) {
        float attackCooldown;
        int p;
        int o;
        int n2;
        PlayerEntity playerEntity = getCameraPlayer();
        if(playerEntity == null) {
            return;
        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShader(GameRenderer :: getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
        ItemStack itemStack = playerEntity.getOffHandStack();
        Arm arm = playerEntity.getMainArm().getOpposite();
        int halfScaleWidth = scaledWidth / 2;
        int zOffset = getZOffset();
        int k = 182;
        int l = 91;
        // rending item bar
        setZOffset(- 90);
        drawTexture(matrices, halfScaleWidth - 91, this.scaledHeight - 22, 0, 0, 182, 22);
        drawTexture(matrices, halfScaleWidth - 91 - 1 + playerEntity.getInventory().selectedSlot * 20, this.scaledHeight - 22 - 1, 0, 22, 24, 22);
        if(! itemStack.isEmpty()) {
            if(arm == Arm.LEFT) {
                this.drawTexture(matrices, halfScaleWidth - 91 - 29, this.scaledHeight - 23, 24, 22, 29, 24);
            } else {
                this.drawTexture(matrices, halfScaleWidth + 91, this.scaledHeight - 23, 53, 22, 29, 24);
            }
        }

        // rending health bar
        setZOffset(zOffset);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        int m = 1;
        for(n2 = 0; n2 < 9; ++ n2) {
            o = halfScaleWidth - 90 + n2 * 20 + 2;
            p = scaledHeight - 16 - 3;
            this.renderHotbarItem(o, p, tickDelta, playerEntity, playerEntity.getInventory().main.get(n2), m++);
        }
        if(! itemStack.isEmpty()) {
            n2 = scaledHeight - 16 - 3;
            if(arm == Arm.LEFT) {
                this.renderHotbarItem(halfScaleWidth - 91 - 26, n2, tickDelta, playerEntity, itemStack, m++);
            } else {
                this.renderHotbarItem(halfScaleWidth + 91 + 10, n2, tickDelta, playerEntity, itemStack, m++);
            }
        }
        if(this.client.options.attackIndicator == AttackIndicator.HOTBAR && (attackCooldown = this.client.player.getAttackCooldownProgress(0.0f)) < 1.0f) {
            o = scaledHeight - 20;
            p = halfScaleWidth + 91 + 6;
            if(arm == Arm.RIGHT) {
                p = halfScaleWidth - 91 - 22;
            }
            RenderSystem.setShaderTexture(0, DrawableHelper.GUI_ICONS_TEXTURE);
            int q = (int) (attackCooldown * 19.0f);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            this.drawTexture(matrices, p, o, 0, 94, 18, 18);
            this.drawTexture(matrices, p, o + 18 - q, 18, 112 - q, 18, q);
        }
        RenderSystem.disableBlend();
    }
}