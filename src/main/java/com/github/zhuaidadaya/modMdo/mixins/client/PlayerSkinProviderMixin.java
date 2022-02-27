package com.github.zhuaidadaya.modMdo.mixins.client;

import com.google.common.hash.Hashing;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.texture.*;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.File;

@Mixin(PlayerSkinProvider.class)
public class PlayerSkinProviderMixin {

    @Shadow @Final private TextureManager textureManager;

    @Shadow @Final private File skinCacheDir;

    /**
     * @author
     * @reason
     */
    @Overwrite
    private Identifier loadSkin(MinecraftProfileTexture profileTexture, MinecraftProfileTexture.Type type, @Nullable PlayerSkinProvider.SkinTextureAvailableCallback callback) {
        String string = Hashing.sha1().hashUnencodedChars(profileTexture.getHash()).toString();
        Identifier identifier = new Identifier("skins/" + string);
        AbstractTexture abstractTexture = textureManager.getOrDefault(identifier, MissingSprite.getMissingSpriteTexture());
        if (abstractTexture == MissingSprite.getMissingSpriteTexture()) {
            File file = new File(this.skinCacheDir, string.length() > 2 ? string.substring(0, 2) : "xx");
            File file2 = new File(file, string);
            PlayerSkinTexture playerSkinTexture = new PlayerSkinTexture(file2, profileTexture.getUrl(), DefaultSkinHelper.getTexture(), type == MinecraftProfileTexture.Type.SKIN, () -> {
                if (callback != null) {
                    callback.onSkinTextureAvailable(type, identifier, profileTexture);
                }
            });
            System.out.println(identifier);
            this.textureManager.registerTexture(identifier, playerSkinTexture);
        } else if (callback != null) {
            callback.onSkinTextureAvailable(type, identifier, profileTexture);
        }
        return identifier;
    }
}
