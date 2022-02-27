package com.github.zhuaidadaya.modMdo.mixins.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

@Mixin(PlayerSkinTexture.class)
public abstract class PlayerSkinTextureMixin extends ResourceTexture {
    @Shadow
    private boolean loaded;

    @Shadow
    private @Nullable CompletableFuture<?> loader;

    @Shadow
    @Final
    private static Logger LOGGER;

    @Shadow
    @Final
    private @Nullable File cacheFile;

    @Shadow
    @Nullable
    protected abstract NativeImage loadTexture(InputStream stream);

    @Shadow
    protected abstract void onTextureLoaded(NativeImage image);

    @Shadow
    @Final
    private String url;

    public PlayerSkinTextureMixin(Identifier location) {
        super(location);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void load(ResourceManager manager) throws IOException {
        MinecraftClient.getInstance().execute(() -> {
            if (!this.loaded) {
                try {
                    super.load(manager);
                } catch (IOException var3) {
                    LOGGER.warn("Failed to load texture: {}", this.location, var3);
                }

                this.loaded = true;
            }

        });
        if (this.loader == null) {
            NativeImage nativeImage;
            if (this.cacheFile != null && this.cacheFile.isFile()) {
                LOGGER.debug("Loading http texture from local cache ({})", this.cacheFile);
                FileInputStream fileInputStream = new FileInputStream(this.cacheFile);
                nativeImage = this.loadTexture(fileInputStream);
            } else {
                nativeImage = null;
            }

            if (nativeImage != null) {
                this.onTextureLoaded(nativeImage);
            } else {
                this.loader = CompletableFuture.runAsync(() -> {
                    HttpURLConnection httpURLConnection = null;
                    LOGGER.debug("Downloading http texture from {} to {}", this.url, this.cacheFile);

                    try {
                        if (url.startsWith("file://")) {
                            BufferedInputStream reader = new BufferedInputStream(new FileInputStream(url.substring(7)));
                            MinecraftClient.getInstance().execute(() -> {
                                NativeImage nativeImage2 = this.loadTexture(reader);
                                if (nativeImage2 != null) {
                                    this.onTextureLoaded(nativeImage2);
                                }

                            });
                        } else {
                            httpURLConnection = (HttpURLConnection) (new URL(this.url)).openConnection(MinecraftClient.getInstance().getNetworkProxy());
                            httpURLConnection.setDoInput(true);
                            httpURLConnection.setDoOutput(false);
                            httpURLConnection.connect();
                            if (httpURLConnection.getResponseCode() / 100 == 2) {
                                InputStream inputStream;
                                if (this.cacheFile != null) {
                                    FileUtils.copyInputStreamToFile(httpURLConnection.getInputStream(), this.cacheFile);
                                    inputStream = new FileInputStream(this.cacheFile);
                                } else {
                                    inputStream = httpURLConnection.getInputStream();
                                }

                                MinecraftClient.getInstance().execute(() -> {
                                    NativeImage nativeImage2 = this.loadTexture(inputStream);
                                    if (nativeImage2 != null) {
                                        this.onTextureLoaded(nativeImage2);
                                    }

                                });
                            }
                        }
                    } catch (Exception var6) {
                        LOGGER.error("Couldn't download texture", var6);
                    } finally {
                        if (httpURLConnection != null) {
                            httpURLConnection.disconnect();
                        }

                    }

                }, Util.getMainWorkerExecutor());
            }
        }
    }
}
