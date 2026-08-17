package com.hoz.ds_tacz_compat;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

/**
 * Debug hotkey to open this mod's client config screen directly. Defaults to unbound.
 */
public final class KeyBinds {
    public static final KeyMapping OPEN_CONFIG = new KeyMapping(
            "key.ds_tacz_compat.open_config",
            InputConstants.UNKNOWN.getValue(),
            "key.categories.ds_tacz_compat");

    private KeyBinds() {}

    public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_CONFIG);
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        while (OPEN_CONFIG.consumeClick()) {
            Minecraft mc = Minecraft.getInstance();
            mc.setScreen(ModList.get().isLoaded("cloth_config")
                    ? ClothConfigScreen.create(mc.screen)
                    : new ClothConfigWarningScreen(mc.screen));
        }
    }
}
