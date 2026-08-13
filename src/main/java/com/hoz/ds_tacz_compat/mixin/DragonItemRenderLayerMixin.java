package com.hoz.ds_tacz_compat.mixin;

import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRenderer;
import by.dragonsurvivalteam.dragonsurvival.client.render.entity.dragon.DragonItemRenderLayer;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import by.dragonsurvivalteam.dragonsurvival.compat.bettercombat.BetterCombat;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.MovementData;
import com.hoz.ds_tacz_compat.Config;
import com.hoz.ds_tacz_compat.GunRenderData;
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

    static {
        try {
            MUZZLE_FLASH_START_MARK = MuzzleFlashRender.class.getDeclaredField("muzzleFlashStartMark");
            MUZZLE_FLASH_START_MARK.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access MuzzleFlashRender.muzzleFlashStartMark", e);
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

        if (GunRenderData.worldRenderDepth <= 0) {
            ci.cancel();
            return;
        }

        Player player = animatable.getPlayer();
        if (player == null) {
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
        if (player == mc.player && mc.options.getCameraType().isFirstPerson()) {
            ci.cancel();
            return;
        }

        if (!bone.getName().equals("RightItem")) {
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
        float height = animatable.getBbHeight() * animatable.getScale() + Config.GUN_HEIGHT_OFFSET.get().floatValue() + floatAnim;
        GunRenderData.setHeadGunY(player.getUUID(), height);
        float offsetX = Config.GUN_OFFSET_X.get().floatValue();
        float offsetZ = Config.GUN_OFFSET_Z.get().floatValue();

        // Level 1: body-aligned coordinate system for position offsets
        float bodyYaw = (float) MovementData.getData(player).bodyYaw;
        poseStack.mulPose(Axis.YN.rotationDegrees(bodyYaw));
        poseStack.translate(offsetX, height, offsetZ);

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

        // Level 2: camera-oriented rendering
        poseStack.mulPose(Axis.YP.rotationDegrees(180 - state.yaw + bodyYaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(-state.pitch));
        poseStack.scale(0.8f, 0.8f, 0.8f);

        // Miniguns have [90,0,0] on their thirdperson_hand bone; pre-cancel it
        IGun iGun = (IGun) stack.getItem();
        if (iGun.getGunId(stack).getPath().contains("minigun")) {
            poseStack.mulPose(Axis.XP.rotationDegrees(-90));
        }

        try {
            MUZZLE_FLASH_START_MARK.setBoolean(null, true);
        } catch (IllegalAccessException ignored) {
        }

        boolean isSelf = (player == mc.player);
        MuzzleFlashRender.isSelf = isSelf;
        ShellRender.isSelf = isSelf;

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
