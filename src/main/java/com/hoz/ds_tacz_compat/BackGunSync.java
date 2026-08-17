package com.hoz.ds_tacz_compat;

import com.tacz.guns.api.item.IGun;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Back-gun selection shared between client and server, plus the client-side cache of remote
 * dragon players' back-gun items received from the server.
 */
public final class BackGunSync {
    private static final Map<Integer, ItemStack> CLIENT_CACHE = new HashMap<>();

    private BackGunSync() {}

    /** First gun in the hotbar (left to right) that is not the selected slot. */
    public static ItemStack findBackGunItem(Player player) {
        Inventory inventory = player.getInventory();
        for (int i = 0; i < 9; i++) {
            if (i == inventory.selected) {
                continue;
            }
            ItemStack stack = inventory.getItem(i);
            if (stack.getItem() instanceof IGun) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    public static void clientCache(int entityId, ItemStack stack) {
        CLIENT_CACHE.put(entityId, stack);
    }

    public static ItemStack clientBackGun(int entityId) {
        return CLIENT_CACHE.getOrDefault(entityId, ItemStack.EMPTY);
    }
}
