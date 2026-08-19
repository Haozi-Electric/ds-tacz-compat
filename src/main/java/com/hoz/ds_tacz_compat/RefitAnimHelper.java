package com.hoz.ds_tacz_compat;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import com.tacz.guns.client.gui.GunRefitScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class RefitAnimHelper {
    private RefitAnimHelper() {}

    public static boolean isDragonRefitScreen() {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        boolean dragon = player != null && DragonStateProvider.isDragon(player);
        boolean refit = mc.screen instanceof GunRefitScreen;
        System.out.println("[ds_tacz_compat][refit-anim] isDragonRefitScreen dragon=" + dragon
                + " player=" + (player == null ? "null" : player.getName().getString())
                + " screen=" + (mc.screen == null ? "null" : mc.screen.getClass().getName())
                + " refit=" + refit);
        return dragon && refit;
    }
}
