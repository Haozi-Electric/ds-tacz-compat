package com.hoz.ds_tacz_compat;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Cloth has no double slider, so this combines a slider with a decimal text field plus a reset
 * button. Dragging the slider and typing in the field stay in sync and both write through
 * {@code saveConsumer}.
 */
public class DoubleSliderEntry extends AbstractConfigListEntry<Double> {
    private final double min;
    private final double max;
    private final double defaultValue;
    private final Consumer<Double> saveConsumer;
    private final Slider slider;
    private final EditBox textField;
    private final Button resetButton;
    private boolean updating = false;

    public DoubleSliderEntry(Component name, double value, double min, double max, double defaultValue, Consumer<Double> saveConsumer) {
        super(name, false);
        this.min = min;
        this.max = max;
        this.defaultValue = defaultValue;
        this.saveConsumer = saveConsumer;
        this.slider = new Slider(0, 0, 100, 20, value);
        this.textField = new EditBox(Minecraft.getInstance().font, 0, 0, 56, 20, Component.empty());
        this.textField.setMaxLength(16);
        this.textField.setValue(format(value));
        this.textField.setResponder(s -> {
            if (updating) {
                return;
            }
            try {
                double parsed = Double.parseDouble(s);
                double clamped = Mth.clamp(parsed, min, max);
                saveConsumer.accept(clamped);
                updating = true;
                slider.setDouble(clamped);
                updating = false;
            } catch (NumberFormatException ignored) {
            }
        });
        this.resetButton = Button.builder(Component.literal("↺"), b -> {
                    double v = this.defaultValue;
                    saveConsumer.accept(v);
                    updating = true;
                    slider.setDouble(v);
                    textField.setValue(format(v));
                    updating = false;
                }).bounds(0, 0, 20, 20).build();
    }

    private static String format(double v) {
        return String.format("%.2f", v);
    }

    @Override
    public Double getValue() {
        return slider.doubleValue;
    }

    @Override
    public Optional<Double> getDefaultValue() {
        return Optional.of(defaultValue);
    }

    @Override
    public void render(GuiGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight,
                       int mouseX, int mouseY, boolean isHovered, float delta) {
        super.render(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
        graphics.drawString(Minecraft.getInstance().font, getFieldName(), x, y + 6, getPreferredTextColor());
        resetButton.setX(x + entryWidth - 20);
        resetButton.setY(y);
        resetButton.render(graphics, mouseX, mouseY, delta);
        slider.setX(x + entryWidth - 176);
        slider.setY(y);
        slider.render(graphics, mouseX, mouseY, delta);
        textField.setX(x + entryWidth - 76);
        textField.setY(y);
        textField.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return List.of(slider, textField, resetButton);
    }

    @Override
    public List<? extends NarratableEntry> narratables() {
        return List.of(slider, textField, resetButton);
    }

    private class Slider extends AbstractSliderButton {
        private double doubleValue;

        Slider(int x, int y, int width, int height, double value) {
            super(x, y, width, height, Component.empty(), 0.0);
            this.doubleValue = value;
            this.value = (value - min) / (max - min);
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            setMessage(Component.literal(format(doubleValue)));
        }

        @Override
        protected void applyValue() {
            doubleValue = Mth.lerp(this.value, min, max);
            saveConsumer.accept(doubleValue);
            updating = true;
            textField.setValue(format(doubleValue));
            updating = false;
        }

        void setDouble(double v) {
            this.doubleValue = v;
            this.value = (v - min) / (max - min);
            updateMessage();
        }
    }
}
