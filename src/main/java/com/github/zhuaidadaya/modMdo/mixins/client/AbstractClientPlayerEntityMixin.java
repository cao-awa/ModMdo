package com.github.zhuaidadaya.modMdo.mixins.client;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.regex.Pattern;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin extends PlayerEntity {
    public AbstractClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Shadow protected abstract @Nullable PlayerListEntry getPlayerListEntry();

    private static final Pattern FORMATTING_CODE = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");

//    /**
//     * @author
//     * @reason
//     */
//    @Overwrite
//    private static void loadSkin(Identifier id, String name) {
//        TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
//        AbstractTexture abstractTexture = textureManager.getOrDefault(id, MissingSprite.getMissingSpriteTexture());
//        System.out.println(id);
//        if (abstractTexture == MissingSprite.getMissingSpriteTexture()) {
//            abstractTexture = new PlayerSkinTexture(null, String.format("file://" + System.getProperty("user.dir") + "/", stripTextFormat(name)), DefaultSkinHelper.getTexture(AbstractClientPlayerEntity.getOfflinePlayerUuid(name)), true, null);
//            textureManager.registerTexture(id, abstractTexture);
//        }
//    }

//    /**
//     * {@return the {@code text} with all formatting codes removed}
//     *
//     * <p>A formatting code is the character {@code \u00a7} followed by
//     * a numeric character or a letter A to F, K to O, or R.
//     *
//     * @see Formatting#strip
//     */
//    private static String stripTextFormat(String text) {
//        return FORMATTING_CODE.matcher(text).replaceAll("");
//    }

    @Inject(method = "getSkinTexture",at = @At("HEAD"))
    public void getSkinTexture(CallbackInfoReturnable<Identifier> cir) {
    }
}
