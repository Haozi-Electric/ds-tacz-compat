package com.hoz.ds_tacz_compat;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Shared data between DragonItemRenderLayerMixin and other mixins.
 * Per-player state is keyed by UUID to avoid cross-contamination
 * when multiple dragons render in the same frame.
 */
public final class GunRenderData {
    public static Matrix4f worldMatrix = new Matrix4f();

    // >0 while the main 3D world is rendering; 0 during GUI/HUD redraws (paper doll,
    // shoulder-surfing overlays, etc.). Guards against those replaying entity renders
    // and polluting the shared state below.
    public static int worldRenderDepth;

    private static final Map<UUID, Float> headGunYMap = new HashMap<>();
    private static final Map<UUID, SmoothState> smoothMap = new HashMap<>();

    private GunRenderData() {}

    public static void setHeadGunY(UUID id, float value) { headGunYMap.put(id, value); }
    public static float getHeadGunY(UUID id) { return headGunYMap.getOrDefault(id, 1.0f); }

    public static SmoothState smoothState(UUID id) {
        return smoothMap.computeIfAbsent(id, k -> new SmoothState());
    }

    /**
     * Extension dragon mods replace the vanilla body model; the floating gun and back gun are
     * anchored to vanilla bones (RightItem / Torso) that those models may not have, so the
     * features are disabled when the dragon's body model is on the matching blacklist.
     */
    public static boolean isDragonModelDisabled(Player player) {
        return isModelInList(player, Config.DISABLED_GUN_MODELS.get());
    }

    public static boolean isBackGunModelDisabled(Player player) {
        return isModelInList(player, Config.DISABLED_BACK_GUN_MODELS.get());
    }

    private static boolean isModelInList(Player player, List<? extends String> blacklist) {
        if (!DragonStateProvider.isDragon(player)) {
            return false;
        }
        ResourceLocation model = DragonStateProvider.getData(player).body().value().model();
        for (String entry : blacklist) {
            ResourceLocation id = ResourceLocation.tryParse(entry);
            if (id != null && id.equals(model)) {
                return true;
            }
        }
        return false;
    }

    public static class SmoothState {
        public float yaw, pitch;
        public boolean initialized;
    }
}
