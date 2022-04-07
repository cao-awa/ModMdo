package com.github.zhuaidadaya.modmdo.mixins;

import com.github.zhuaidadaya.modmdo.login.token.ClientEncryptionToken;
import com.github.zhuaidadaya.modmdo.login.token.TokenContentType;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.screen.AddServerScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.github.zhuaidadaya.modmdo.storage.Variables.*;

@Mixin(AddServerScreen.class)
public abstract class AddServerScreenMixin extends Screen {
    @Shadow
    private TextFieldWidget addressField;
    @Shadow
    private ButtonWidget addButton;
    @Shadow
    private TextFieldWidget serverNameField;

    private boolean enableGui = true;

    protected AddServerScreenMixin(Text title) {
        super(title);
    }

    @Shadow
    protected abstract void updateAddButton();

    @Shadow
    protected abstract void addAndClose();

    /**
     * 在编辑服务器的页面添加token的文本栏
     * 以及连接类型的文本栏
     *
     * @param ci callback
     * @author zhuaidadaya
     * @author 草二号机
     * @author 草awa
     */
    @Inject(method = "init", at = @At("RETURN"))
    public void init(CallbackInfo ci) {
        if (configCached.getConfigString("token_editing_gui") == null || configCached.getConfigString("token_editing_gui").equals("enable")) {
            editToken = new TextFieldWidget(textRenderer, width / 2 - 60, 30, 160, 20, new TranslatableText("oops"));
            editToken.setMaxLength(Integer.MAX_VALUE);
            editToken.setText(getModMdoTokenFormat(addressField.getText(), TokenContentType.TOKEN_BY_ENCRYPTION));
            editToken.setChangedListener((address) -> {
                setToken();
            });
            addSelectableChild(editToken);

            editLoginType = new TextFieldWidget(textRenderer, width / 2 + 100, 30, 100, 20, new TranslatableText("oops2"));
            editLoginType.setText(getModMdoTokenFormat(addressField.getText(), TokenContentType.LOGIN_TYPE));
            editLoginType.setChangedListener((validType) -> {
                String loginType = editLoginType.getText();
                addButton.active = (ServerAddress.isValid(this.addressField.getText()) && !this.serverNameField.getText().isEmpty()) & (loginType.equals("default") | loginType.equals("ops"));
            });

            addSelectableChild(editLoginType);

            tokenTip = new TextFieldWidget(textRenderer, width / 2 - 100, 30, 40, 20, new TranslatableText("token"));
            tokenTip.setText("Token");

            enableGui = true;
        } else {
            enableGui = false;
        }
    }

    /**
     * 这里直接新建然后添加token, 因为添加相同的IP会覆盖旧的
     *
     * @author 草awa
     */
    public void setToken() {
        if (enableGui) {
            String address = addressField.getText();
            modMdoToken.addClientToken(new ClientEncryptionToken(editToken.getText(), address.contains(":") ? address : "25565", editLoginType.getText(), VERSION_ID));
            updateModMdoVariables();
        }
    }

    /**
     * 保存服务器信息时同时保存token
     *
     * @param ci callback
     * @author 草awa
     */
    @Inject(method = "addAndClose", at = @At("HEAD"))
    private void addAndClose(CallbackInfo ci) {
        setToken();
    }

    /**
     * 渲染三个新添加的文本栏
     *
     * @param matrices matrices
     * @param mouseX   mouseX
     * @param mouseY   mouseY
     * @param delta    delta
     * @param ci       callback
     * @author zhuaidadaya
     */
    @Inject(method = "render", at = @At("RETURN"))
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (enableGui) {
            editToken.render(matrices, mouseX, mouseY, delta);
            editLoginType.render(matrices, mouseX, mouseY, delta);
            tokenTip.render(matrices, mouseX, mouseY, delta);
        }
    }
}