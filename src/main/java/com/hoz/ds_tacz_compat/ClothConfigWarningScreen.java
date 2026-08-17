package com.hoz.ds_tacz_compat;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

/**
 * Shown when the player opens the config screen but Cloth Config is not installed.
 */
public class ClothConfigWarningScreen extends Screen {
    public static final String CLOTH_CONFIG_URL = "https://www.curseforge.com/minecraft/mc-mods/cloth-config";
    private final Screen lastScreen;
    private MultiLineLabel message = MultiLineLabel.EMPTY;

    public ClothConfigWarningScreen(Screen lastScreen) {
        super(Component.literal("Cloth Config API"));
        this.lastScreen = lastScreen;
    }

    @Override
    protected void init() {
        int posX = (this.width - 200) / 2;
        int posY = this.height / 2;
        this.message = MultiLineLabel.create(this.font, Component.translatable("ds_tacz_compat.cloth_config_warning"), 300);
        this.addRenderableWidget(Button.builder(Component.translatable("ds_tacz_compat.cloth_config_download"), b -> openUrl(CLOTH_CONFIG_URL))
                .bounds(posX, posY - 15, 200, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, b -> Minecraft.getInstance().setScreen(this.lastScreen))
                .bounds(posX, posY + 50, 200, 20).build());
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(gui, mouseX, mouseY, partialTick);
        this.message.renderCentered(gui, this.width / 2, 80);
        super.render(gui, mouseX, mouseY, partialTick);
    }

    private void openUrl(String url) {
        if (minecraft != null) {
            minecraft.setScreen(new ConfirmLinkScreen(yes -> {
                if (yes) {
                    Util.getPlatform().openUri(url);
                }
                minecraft.setScreen(this);
            }, url, true));
        }
    }
}
