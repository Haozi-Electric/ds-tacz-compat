package com.hoz.ds_tacz_compat;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue HIDE_FIRST_PERSON_ARMS = BUILDER
            .comment("Hide first-person dragon arms when holding TACZ guns")
            .translation("ds_tacz_compat.configuration.hideFirstPersonArms")
            .define("hideFirstPersonArms", true);

    public static final ModConfigSpec.BooleanValue HIDE_THIRD_PERSON_GUNS = BUILDER
            .comment("If true, hide third-person guns on dragons entirely.\nIf false, render the gun floating above the dragon's head.")
            .translation("ds_tacz_compat.configuration.hideThirdPersonGuns")
            .define("hideThirdPersonGuns", false);

    public static final ModConfigSpec.DoubleValue GUN_HEIGHT_OFFSET = BUILDER
            .comment("Height offset above the dragon's visual head, in blocks.")
            .translation("ds_tacz_compat.configuration.gunHeightOffset")
            .defineInRange("gunHeightOffset", 1.0, -2.0, 5.0);

    public static final ModConfigSpec.DoubleValue GUN_FLOAT_SPEED = BUILDER
            .comment("Vertical bobbing speed of the floating gun, in full float cycles per second. 0 = no bobbing. Higher = faster.")
            .translation("ds_tacz_compat.configuration.gunFloatSpeed")
            .defineInRange("gunFloatSpeed", 1.0, 0.0, 5.0);

    public static final ModConfigSpec.DoubleValue GUN_OFFSET_X = BUILDER
            .comment("Left/right offset of the floating gun, in blocks. Positive = right.")
            .translation("ds_tacz_compat.configuration.gunOffsetX")
            .defineInRange("gunOffsetX", 0.0, -5.0, 5.0);

    public static final ModConfigSpec.DoubleValue GUN_OFFSET_Z = BUILDER
            .comment("Forward/back offset of the floating gun, in blocks. Positive = forward.")
            .translation("ds_tacz_compat.configuration.gunOffsetZ")
            .defineInRange("gunOffsetZ", 0.0, -5.0, 5.0);

    public static final ModConfigSpec.DoubleValue AIM_SMOOTHING = BUILDER
            .comment("EMA smoothing time for the gun's aim-follow animation, in seconds. 0 = instant.")
            .translation("ds_tacz_compat.configuration.aimSmoothing")
            .defineInRange("aimSmoothing", 0.15, 0.0, 2.0);

    public static final ModConfigSpec.DoubleValue GUN_PITCH_CLAMP = BUILDER
            .comment("Maximum downward pitch of the gun, in degrees below horizontal. Prevents the gun aiming at your own feet.")
            .translation("ds_tacz_compat.configuration.gunPitchClamp")
            .defineInRange("gunPitchClamp", 60.0, 0.0, 90.0);

    public static final ModConfigSpec.BooleanValue TRACER_VISIBLE = BUILDER
            .comment("Show bullet tracers for dragon players, shifted to the head gun muzzle.")
            .translation("ds_tacz_compat.configuration.tracerVisible")
            .define("tracerVisible", true);

    public static final ModConfigSpec.BooleanValue HIDE_REMOTE_TRACER = BUILDER
            .comment("Hide bullet tracers from other dragon players, matching original TACZ behavior.")
            .translation("ds_tacz_compat.configuration.hideRemoteTracer")
            .define("hideRemoteTracer", true);

    static final ModConfigSpec SPEC = BUILDER.build();
}
