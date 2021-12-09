package com.github.zhuaidadaya.modMdo.configure.modmdo;

import net.minecraft.client.gui.screen.ConfirmChatLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.option.NarratorOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Option;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

public class ModMdoOptionsScreen extends NarratorOptionsScreen {
    private static final Option[] OPTIONS = new Option[]{
            ModMdoOption.TEST
    };
    private static final String HELP_URL = "https://github.com/zhuaidadaya/ModMdo/wiki";

    public ModMdoOptionsScreen(Screen parent, GameOptions gameOptions) {
        super(parent, gameOptions, new TranslatableText("options.modmdo.title"), OPTIONS);
    }

    @Override
    protected void initFooter() {
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 155, this.height - 27, 150, 20, new TranslatableText("options.modmdo.link"), button -> this.client.setScreen(new ConfirmChatLinkScreen(openInBrowser -> {
            if(openInBrowser) {
                Util.getOperatingSystem().open(HELP_URL);
            }
            this.client.setScreen(this);
        }, HELP_URL, true))));
        this.addDrawableChild(new ButtonWidget(this.width / 2 + 5, this.height - 27, 150, 20, ScreenTexts.DONE, button -> this.client.setScreen(this.parent)));
    }
}
