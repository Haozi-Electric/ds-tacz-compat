package com.hoz.ds_tacz_compat;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * A button that wipes the whole per-model list and restores the tuned defaults shipped in the
 * mod jar (default_dragon_models.json), after an explicit confirmation.
 */
public class ResetDefaultsEntry extends AbstractConfigListEntry<Boolean> {
    private final Screen parent;
    private final Button button;

    public ResetDefaultsEntry(Component name, Screen parent) {
        super(name, true);
        this.parent = parent;
        this.button = Button.builder(name, b -> openConfirm()).bounds(0, 0, 150, 20).build();
    }

    private void openConfirm() {
        Minecraft mc = Minecraft.getInstance();
        mc.setScreen(new ConfirmScreen(confirmed -> {
            if (confirmed) {
                ClothConfigScreen.resetDragonModelsToDefaultList();
                mc.setScreen(ClothConfigScreen.create(parent));
            } else {
                mc.setScreen(parent);
            }
        }, Component.translatable("ds_tacz_compat.config.model_tool.reset_list_title"),
           Component.translatable("ds_tacz_compat.config.model_tool.reset_list_warning").withColor(0xFFFF5555)));
    }

    @Override
    @NotNull
    public List<? extends GuiEventListener> children() {
        return List.of(button);
    }

    @Override
    public List<? extends NarratableEntry> narratables() {
        return List.of(button);
    }

    @Override
    public Boolean getValue() {
        return true;
    }

    @Override
    public Optional<Boolean> getDefaultValue() {
        return Optional.of(true);
    }

    @Override
    public void render(GuiGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        button.setX(x + entryWidth - 150);
        button.setY(y);
        button.render(graphics, mouseX, mouseY, delta);
        super.render(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
    }
}
