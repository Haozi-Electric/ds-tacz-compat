package com.hoz.ds_tacz_compat;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(TaczArmHide.MODID)
public class TaczArmHide {
    public static final String MODID = "ds_tacz_compat";
    public static final Logger LOGGER = LogUtils.getLogger();

    public TaczArmHide(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        modContainer.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
        if (FMLEnvironment.dist.isClient()) {
            if (ModList.get().isLoaded("cloth_config")) {
                modContainer.registerExtensionPoint(IConfigScreenFactory.class, (container, screen) -> ClothConfigScreen.create(screen));
            } else {
                modContainer.registerExtensionPoint(IConfigScreenFactory.class, (container, screen) -> new ClothConfigWarningScreen(screen));
            }
            modEventBus.addListener(KeyBinds::registerKeyBindings);
            NeoForge.EVENT_BUS.addListener(KeyBinds::onClientTick);
        }
    }
}
