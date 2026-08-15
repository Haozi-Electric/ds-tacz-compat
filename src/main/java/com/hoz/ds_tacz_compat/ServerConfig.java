package com.hoz.ds_tacz_compat;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ServerConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue GLIDING_SHOOTING = BUILDER
            .comment("Allow dragons to shoot while gliding. If false, aiming or reloading exits flight.")
            .translation("ds_tacz_compat.configuration.glidingShooting")
            .define("glidingShooting", true);

    public static final ModConfigSpec.DoubleValue SPEED_INFLUENCE_MULTIPLIER = BUILDER
            .comment("Multiplier applied to TACZ's movement-speed influence on dragons. 0 cancels it, 1 keeps vanilla, 2 doubles it.")
            .translation("ds_tacz_compat.configuration.speedInfluenceMultiplier")
            .defineInRange("speedInfluenceMultiplier", 1.0, 0.0, 3.0);

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
}
