package com.hoz.ds_tacz_compat;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.concurrent.ConcurrentHashMap;

/**
 * DragonSurvival switches the dragon's flight/crouch pose through
 * {@code Player#setForcedPose} (tracked in {@code DragonStateHandler#previousPose}),
 * which does not trigger {@code refreshDimensions}. The eye height / collision box
 * then only re-syncs while growth is still lerping. At max growth the lerp stops,
 * so a walk/fly switch leaves {@code getEyeHeight()} stale and the bullet spawn
 * height drifts. Re-sync the dimensions as soon as the effective pose changes.
 */
@EventBusSubscriber
public final class PoseRecalibrator {
    private static final ConcurrentHashMap<String, Pose> LAST_POSE = new ConcurrentHashMap<>();

    private PoseRecalibrator() {}

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (!DragonStateProvider.isDragon(player)) {
            LAST_POSE.remove(key(player));
            return;
        }

        Pose pose = DragonStateProvider.getData(player).previousPose;
        if (pose == null) {
            return;
        }

        Pose previous = LAST_POSE.put(key(player), pose);
        if (previous != null && previous != pose) {
            player.refreshDimensions();
        }
    }

    private static String key(Player player) {
        return player.getId() + (player.level().isClientSide() ? "_c" : "_s");
    }
}
