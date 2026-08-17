package com.hoz.ds_tacz_compat;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ServerConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue GLIDING_SHOOTING = BUILDER
            .comment("Allow dragons to shoot while gliding. If false, aiming or reloading exits flight.")
            .translation("ds_tacz_compat.configuration.glidingShooting")
            .define("glidingShooting", true);

    public static final ModConfigSpec.DoubleValue SPEED_INFLUENCE_MULTIPLIER = BUILDER
            .comment("Multiplier applied to TACZ's movement-speed influence on dragons. Defaults to 1; set to 0 to disable the TACZ speed penalty.")
            .translation("ds_tacz_compat.configuration.speedInfluenceMultiplier")
            .defineInRange("speedInfluenceMultiplier", 1.0, 0.0, 3.0);

    public static final ModConfigSpec.BooleanValue SYNC_BACK_GUN_TO_CLIENTS = BUILDER
            .comment("Sync dragon players' back-gun item to nearby clients so remote dragons show it.")
            .translation("ds_tacz_compat.configuration.syncBackGunToClients")
            .define("syncBackGunToClients", true);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private ServerConfig() {}

    // When a client joins a server without this mod, the server config is never synced
    // and stays unloaded; fall back to the defaults instead of throwing.
    public static boolean isGlidingShootingEnabled() {
        return !SPEC.isLoaded() || GLIDING_SHOOTING.get();
    }

    public static double speedInfluenceMultiplier() {
        return SPEC.isLoaded() ? SPEED_INFLUENCE_MULTIPLIER.get() : 1.0;
    }

    public static boolean isSyncBackGunToClients() {
        return !SPEC.isLoaded() || SYNC_BACK_GUN_TO_CLIENTS.get();
    }
}
