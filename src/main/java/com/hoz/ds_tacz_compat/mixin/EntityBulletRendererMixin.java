package com.hoz.ds_tacz_compat.mixin;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.MovementData;
import com.hoz.ds_tacz_compat.Config;
import com.hoz.ds_tacz_compat.GunRenderData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tacz.guns.client.renderer.entity.EntityBulletRenderer;
import com.tacz.guns.entity.EntityKineticBullet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityBulletRenderer.class, remap = false)
public class EntityBulletRendererMixin {

    @Inject(method = "renderTracerAmmo", at = @At("HEAD"), cancellable = true, remap = false)
    private void ds_tacz_compat$hideTracerForDragon(EntityKineticBullet bullet, float[] tracerColor,
                                                     float partialTicks, PoseStack poseStack,
                                                     int packedLight, CallbackInfo ci) {
        if (Config.HIDE_THIRD_PERSON_GUNS.get()) {
            return;
        }

        Entity shooter = bullet.getOwner();
        if (!(shooter instanceof Player player) || !DragonStateProvider.isDragon(player)) {
            return;
        }

        if (player != Minecraft.getInstance().player) {
            if (Config.HIDE_REMOTE_TRACER.get()) {
                ci.cancel();
            }
        } else if (!Config.TRACER_VISIBLE.get()) {
            ci.cancel();
        }
    }

    @Inject(method = "render", at = @At("HEAD"), remap = false)
    private void ds_tacz_compat$shiftTracerForDragon(EntityKineticBullet bullet, float entityYaw,
                                                      float partialTicks, PoseStack poseStack,
                                                      MultiBufferSource buffer, int packedLight,
                                                      CallbackInfo ci) {
        if (Config.HIDE_THIRD_PERSON_GUNS.get()) {
            return;
        }
        if (!Config.TRACER_VISIBLE.get()) {
            return;
        }
        if (Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
            return;
        }

        Entity shooter = bullet.getOwner();
        if (!(shooter instanceof Player player)) {
            return;
        }
        if (player != Minecraft.getInstance().player) {
            return;
        }
        if (!DragonStateProvider.isDragon(player)) {
            return;
        }

        float shiftY = GunRenderData.getHeadGunY(player.getUUID()) - player.getEyeHeight() + 0.3f;

        // Position offsets in body-aligned frame (matches gun rendering Level 1)
        float bodyYaw = (float) MovementData.getData(player).bodyYaw;
        float yawRad = (float) Math.toRadians(bodyYaw);
        float offsetScale = 1.0f;
        if (Config.GUN_OFFSET_SCALE_WITH_DRAGON.get()) {
            offsetScale = (float) DragonStateProvider.getData(player).getVisualScale(player, partialTicks);
        }
        float offsetX = Config.GUN_OFFSET_X.get().floatValue() * offsetScale;
        float offsetZ = Config.GUN_OFFSET_Z.get().floatValue() * offsetScale;
        // YN(bodyYaw) maps body frame to camera space: R_Y(-bodyYaw)
        float cos = (float) Math.cos(yawRad);
        float sin = (float) Math.sin(yawRad);
        float shiftX = offsetX * cos - offsetZ * sin;
        float shiftZ = offsetX * sin + offsetZ * cos;

        poseStack.translate(shiftX, shiftY, shiftZ);
    }
}
