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
            .defineInRange("gunFloatSpeed", 0.5, 0.0, 5.0);

    public static final ModConfigSpec.DoubleValue GUN_BASE_SCALE = BUILDER
            .comment("Base scale of the floating gun. 1.0 matches the item's original size.")
            .translation("ds_tacz_compat.configuration.gunBaseScale")
            .defineInRange("gunBaseScale", 0.8, 0.1, 5.0);

    public static final ModConfigSpec.BooleanValue GUN_SCALE_WITH_DRAGON = BUILDER
            .comment("Scale the floating gun with the dragon's size, matching DragonSurvival's third-person item behavior.")
            .translation("ds_tacz_compat.configuration.gunScaleWithDragon")
            .define("gunScaleWithDragon", true);

    public static final ModConfigSpec.BooleanValue GUN_OFFSET_SCALE_WITH_DRAGON = BUILDER
            .comment("Scale the floating gun's positional offsets (left/right, forward/back, height) with the dragon's size.")
            .translation("ds_tacz_compat.configuration.gunOffsetScaleWithDragon")
            .define("gunOffsetScaleWithDragon", true);

    public static final ModConfigSpec.DoubleValue GUN_OFFSET_X = BUILDER
            .comment("Left/right offset of the floating gun, in blocks. Positive = left.")
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

    public static final ModConfigSpec.BooleanValue FORCE_SHOW_CROSSHAIR = BUILDER
            .comment("Force the TACZ crosshair to show in third person.")
            .translation("ds_tacz_compat.configuration.forceShowCrosshair")
            .define("forceShowCrosshair", true);

    public static final ModConfigSpec.BooleanValue ADS_FEATURE_ENABLED = BUILDER
            .comment("When a dragon aims (right-click) in third person, show the TACZ crosshair and fade the dragon model so the head doesn't block it.")
            .translation("ds_tacz_compat.configuration.adsFeatureEnabled")
            .define("adsFeatureEnabled", true);

    public static final ModConfigSpec.DoubleValue ADS_FADE_AMOUNT = BUILDER
            .comment("How much the dragon model fades while aiming in third person. 0 = opaque, 1 = fully transparent.")
            .translation("ds_tacz_compat.configuration.adsFadeAmount")
            .defineInRange("adsFadeAmount", 0.8, 0.0, 1.0);

    public static final ModConfigSpec.BooleanValue RECOIL_CANCEL_ENABLED = BUILDER
            .comment("Master switch for the no-recoil feature for dragon players.")
            .translation("ds_tacz_compat.configuration.recoilCancelEnabled")
            .define("recoilCancelEnabled", true);

    public static final ModConfigSpec.EnumValue<RecoilCancelScope> RECOIL_CANCEL_SCOPE = BUILDER
            .comment("Cancel camera recoil for dragon players. GLIDING = while gliding, FLYING = in any flight mode, ALWAYS = always. WARNING: FLYING and ALWAYS may break game fairness.")
            .translation("ds_tacz_compat.configuration.recoilCancelScope")
            .defineEnum("recoilCancelScope", RecoilCancelScope.GLIDING);

    public static final ModConfigSpec.BooleanValue BACK_GUN_ENABLED = BUILDER
            .comment("Render hotbar guns on the dragon's back, anchored to the torso bone.")
            .translation("ds_tacz_compat.configuration.backGunEnabled")
            .define("backGunEnabled", true);

    public static final ModConfigSpec.DoubleValue BACK_GUN_SCALE = BUILDER
            .comment("Back gun uniform scale.")
            .translation("ds_tacz_compat.configuration.backGunScale")
            .defineInRange("backGunScale", 0.5, 0.1, 5.0);

    static final ModConfigSpec SPEC = BUILDER.build();

    public enum RecoilCancelScope {
        GLIDING, FLYING, ALWAYS
    }
}
