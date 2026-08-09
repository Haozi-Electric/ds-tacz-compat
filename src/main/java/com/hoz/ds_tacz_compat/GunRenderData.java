package com.hoz.ds_tacz_compat;

import org.joml.Matrix4f;

/**
 * Shared data between DragonItemRenderLayerMixin and other mixins.
 * Updated each frame when rendering the dragon's head gun.
 */
public final class GunRenderData {
    public static Matrix4f worldMatrix = new Matrix4f();
    public static float headGunY;
    /** EMA-smoothed yaw/pitch for camera-follow animation. */
    public static float smoothedYaw, smoothedPitch;
    public static boolean smoothingInitialized;

    private GunRenderData() {}
}
