package com.hoz.ds_tacz_compat.mixin;

import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRenderer;
import by.dragonsurvivalteam.dragonsurvival.client.render.entity.dragon.DragonItemRenderLayer;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import by.dragonsurvivalteam.dragonsurvival.compat.bettercombat.BetterCombat;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.MovementData;
import com.hoz.ds_tacz_compat.Config;
import com.hoz.ds_tacz_compat.DragonModelConfig;
import com.hoz.ds_tacz_compat.GunRenderData;
import com.hoz.ds_tacz_compat.RefitAnimHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.client.model.functional.MuzzleFlashRender;
import com.tacz.guns.client.model.functional.ShellRender;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.cache.object.GeoBone;

import java.lang.reflect.Field;

@Mixin(value = DragonItemRenderLayer.class, remap = false)
public abstract class DragonItemRenderLayerMixin {

    private static final Field MUZZLE_FLASH_START_MARK;
    private static final Field SHOOT_TIME_STAMP;

    static {
        try {
            MUZZLE_FLASH_START_MARK = MuzzleFlashRender.class.getDeclaredField("muzzleFlashStartMark");
            MUZZLE_FLASH_START_MARK.setAccessible(true);
            SHOOT_TIME_STAMP = MuzzleFlashRender.class.getDeclaredField("shootTimeStamp");
            SHOOT_TIME_STAMP.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access MuzzleFlashRender fields", e);
        }
    }

    @Inject(method = "renderStackForBone", at = @At("HEAD"), cancellable = true, remap = false)
    private void ds_tacz_compat$renderGunOnHead(PoseStack poseStack, GeoBone bone, ItemStack stack,
                                                 DragonEntity animatable,
                                                 net.minecraft.client.renderer.MultiBufferSource bufferSource,
                                                 float partialTick, int packedLight, int packedOverlay,
                                                 CallbackInfo ci) {
        if (!(stack.getItem() instanceof IGun)) {
            return;
        }

        if (GunRenderData.worldRenderDepth <= 0 && GunRenderData.paperDollRenderDepth <= 0) {
            ci.cancel();
            return;
        }

        Player player = animatable.getPlayer();
        if (player == null) {
            ci.cancel();
            return;
        }

        DragonModelConfig.FloatingGunConfig gunConfig = DragonModelConfig.floatingGunFor(player);
        if (!gunConfig.enabled) {
            ci.cancel();
            return;
        }

        if (BetterCombat.isAttacking(player)) {
            ci.cancel();
            return;
        }
        if (!ClientDragonRenderer.renderHeldItem) {
            ci.cancel();
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        // During the paper doll we render as if in third person regardless of the real camera.
        if (player == mc.player && mc.options.getCameraType().isFirstPerson()
                && GunRenderData.paperDollRenderDepth <= 0) {
            ci.cancel();
            return;
        }

        if (!bone.getName().equals("RightItem")) {
            ci.cancel();
            return;
        }

        // Only hide the local dragon's floating gun while it opens the refit screen; remote
        // dragons don't get the screen-space gun and should keep theirs.
        if (player == mc.player && RefitAnimHelper.isDragonRefitScreen()) {
            ci.cancel();
            return;
        }

        if (Config.HIDE_THIRD_PERSON_GUNS.get()) {
            ci.cancel();
            return;
        }

        ci.cancel();

        poseStack.popPose();
        poseStack.popPose();
        poseStack.popPose();
        poseStack.popPose();
        poseStack.last().pose().set(GunRenderData.worldMatrix);

        if (!(player.getMainHandItem().getItem() instanceof IGun)) {
            ci.cancel();
            return;
        }

        double floatSpeed = Config.GUN_FLOAT_SPEED.get();
        float floatAnim;
        if (floatSpeed <= 0.0) {
            floatAnim = 0.0f;
        } else {
            double floatPeriodMs = 1000.0 / floatSpeed;
            floatAnim = (float) Math.sin((System.currentTimeMillis() % floatPeriodMs) / floatPeriodMs * Math.PI * 2) * 0.06f;
        }
        float dragonScale = animatable.getScale();
        float offsetScale = Config.GUN_OFFSET_SCALE_WITH_DRAGON.get() ? dragonScale : 1.0f;
        float height = animatable.getBbHeight() * dragonScale + gunConfig.offsetY * offsetScale + floatAnim;
        GunRenderData.setHeadGunY(player.getUUID(), height);
        float offsetX = gunConfig.offsetX * offsetScale;
        float offsetZ = gunConfig.offsetZ * offsetScale;

        // Level 1: body-aligned coordinate system for position offsets.
        // In the paper doll the fork's DS compat redirects the dragon body (yBodyRot is
        // locked to the configured pose), so anchor the gun to that redirected body rather
        // than the live MovementData, and face it as if the player looked straight ahead.
        boolean inPaperDoll = GunRenderData.paperDollRenderDepth > 0;
        float bodyYaw = inPaperDoll ? player.yBodyRot : (float) MovementData.getData(player).bodyYaw;
        poseStack.mulPose(Axis.YN.rotationDegrees(bodyYaw));
        poseStack.translate(offsetX, height, offsetZ);

        float renderYaw;
        float renderPitch;
        if (inPaperDoll) {
            // Static preview: snap to the locked body yaw / head pitch so the gun doesn't
            // swing with the live view and stays consistent with the redirected body.
            renderYaw = player.yBodyRot;
            renderPitch = player.getXRot();
        } else {
            // EMA smoothing: gun follows camera with a soft delay
            float targetYaw = player.getViewYRot(partialTick);
            float targetPitch = player.getViewXRot(partialTick);
            float dt = mc.getTimer().getGameTimeDeltaTicks() / 20f;
            float tau = Config.AIM_SMOOTHING.get().floatValue();
            float alpha;
            if (tau <= 0f) {
                alpha = 1f;
            } else {
                alpha = 1f - (float) Math.exp(-dt / tau);
            }

            GunRenderData.SmoothState state = GunRenderData.smoothState(player.getUUID());
            if (!state.initialized) {
                state.yaw = targetYaw;
                state.pitch = targetPitch;
                state.initialized = true;
            } else {
                float yawDiff = targetYaw - state.yaw;
                if (yawDiff > 180) yawDiff -= 360;
                if (yawDiff < -180) yawDiff += 360;
                state.yaw += yawDiff * alpha;
                state.pitch += (targetPitch - state.pitch) * alpha;
            }

            float pitchClamp = Config.GUN_PITCH_CLAMP.get().floatValue();
            if (state.pitch > pitchClamp) {
                state.pitch = pitchClamp;
            }
            renderYaw = state.yaw;
            renderPitch = state.pitch;
        }

        // Level 2: camera-oriented rendering
        poseStack.mulPose(Axis.YP.rotationDegrees(180 - renderYaw + bodyYaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(-renderPitch));
        float baseScale = gunConfig.scale;
        float gunScale = Config.GUN_SCALE_WITH_DRAGON.get() ? animatable.getScale() : 1.0f;
        poseStack.scale(baseScale * gunScale, baseScale * gunScale, baseScale * gunScale);

        // Miniguns have [90,0,0] on their thirdperson_hand bone; pre-cancel it
        IGun iGun = (IGun) stack.getItem();
        if (iGun.getGunId(stack).getPath().contains("minigun")) {
            poseStack.mulPose(Axis.XP.rotationDegrees(-90));
        }

        try {
            MUZZLE_FLASH_START_MARK.setBoolean(null, true);
            // Per-player isolation: only the dragon that just fired gets a muzzle flash.
            SHOOT_TIME_STAMP.setLong(null, GunRenderData.shootTime(player.getUUID()));
        } catch (IllegalAccessException ignored) {
        }

        MuzzleFlashRender.isSelf = true;
        // Shells live in a per-gun-model queue shared across players; only render them for
        // the dragon that recently ejected one, so two players with the same gun don't cross.
        ShellRender.isSelf = GunRenderData.hasRecentShell(player.getUUID());

        mc.getItemRenderer().renderStatic(
                player, stack, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, false,
                poseStack, bufferSource, animatable.level(),
                packedLight, packedOverlay, animatable.getId());

        MuzzleFlashRender.isSelf = false;
        ShellRender.isSelf = false;

        poseStack.pushPose();
        poseStack.pushPose();
        poseStack.pushPose();
        poseStack.pushPose();
    }
}
