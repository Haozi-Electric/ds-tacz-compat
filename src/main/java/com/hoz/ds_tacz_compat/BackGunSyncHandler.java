package com.hoz.ds_tacz_compat;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Server-side: detects when a dragon player's back-gun item changes and pushes it to nearby
 * clients. Disabled when the server-side sync config is off or the server lacks this mod.
 */
@EventBusSubscriber
public final class BackGunSyncHandler {
    private static final Map<UUID, ItemStack> LAST = new HashMap<>();

    private BackGunSyncHandler() {}

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) {
            return;
        }
        if (!ServerConfig.isSyncBackGunToClients()) {
            LAST.remove(player.getUUID());
            return;
        }
        if (!DragonStateProvider.isDragon(player)) {
            LAST.remove(player.getUUID());
            return;
        }
        ItemStack current = BackGunSync.findBackGunItem(player);
        ItemStack previous = LAST.put(player.getUUID(), current);
        if (previous == null || !ItemStack.matches(previous, current)) {
            NetworkHandler.sendBackGun(player, current);
        }
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        Player tracker = event.getEntity();
        if (tracker.level().isClientSide()) {
            return;
        }
        if (!ServerConfig.isSyncBackGunToClients()) {
            return;
        }
        Entity target = event.getTarget();
        if (target instanceof Player targetPlayer && DragonStateProvider.isDragon(targetPlayer)) {
            NetworkHandler.sendBackGunTo(
                    (net.minecraft.server.level.ServerPlayer) tracker,
                    target.getId(),
                    BackGunSync.findBackGunItem(targetPlayer));
        }
    }
}
