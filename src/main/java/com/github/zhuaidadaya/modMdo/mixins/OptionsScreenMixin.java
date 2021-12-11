package com.github.zhuaidadaya.modMdo.mixins;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(OptionsScreen.class)
public class OptionsScreenMixin extends Screen {

    protected OptionsScreenMixin(Text title) {
        super(title);
    }

//    @Inject(at = @At("RETURN"), method = "init")
//    public void init(CallbackInfo c) {
//        this.addDrawableChild(new ButtonWidget(this.width / 2 - 155, this.height / 6 + 144 - 6, 150, 20, new TranslatableText("options.accessibility.title"), button -> this.client.setScreen(new ModMdoOptionsScreen(this, new GameOptions(client, null)))));
//    }
}
