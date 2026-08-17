package com.hoz.ds_tacz_compat;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.event.common.GunFireEvent;
import com.tacz.guns.client.model.BedrockGunModel;
import com.tacz.guns.client.model.functional.ShellRender;
import com.tacz.guns.client.resource.pojo.display.gun.ShellEjection;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

/**
 * TACZ already syncs remote gunfire to the client via GunFireEvent, but its own listener only
 * triggers muzzle flash for the local player. This catches remote dragon shooters and fires
 * their muzzle flash / shell ejection so the floating gun shows the same effects.
 */
@EventBusSubscriber(value = Dist.CLIENT)
public final class RemoteGunEffectHandler {
    private RemoteGunEffectHandler() {}

    @SubscribeEvent
    public static void onGunFire(GunFireEvent event) {
        if (!event.getLogicalSide().isClient()) {
            return;
        }
        LivingEntity shooter = event.getShooter();
        if (!(shooter instanceof Player player)) {
            return;
        }
        if (!DragonStateProvider.isDragon(player)) {
            return;
        }
        if (!Config.SYNC_REMOTE_GUN_EFFECTS.get()) {
            return;
        }
        GunRenderData.recordShoot(shooter.getUUID());
        GunRenderData.recordShell(shooter.getUUID());
        if (shooter != Minecraft.getInstance().player) {
            triggerShell(event.getGunItemStack());
        }
    }

    private static void triggerShell(net.minecraft.world.item.ItemStack gunItem) {
        TimelessAPI.getGunDisplay(gunItem).ifPresent(display -> {
            ShellEjection shellEjection = display.getShellEjection();
            if (shellEjection == null) {
                return;
            }
            BedrockGunModel gunModel = display.getGunModel();
            if (gunModel == null) {
                return;
            }
            ShellRender shellRender = gunModel.getShellRender(0);
            if (shellRender != null) {
                shellRender.addShell(shellEjection.getRandomVelocity());
            }
        });
    }
}
