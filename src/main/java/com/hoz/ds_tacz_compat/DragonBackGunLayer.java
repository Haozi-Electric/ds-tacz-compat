package com.hoz.ds_tacz_compat;

import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.item.IGun;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

/**
 * Renders hotbar guns on the dragon's back, anchored to the {@code Torso} bone, mirroring
 * TACZ's vanilla human back-gun behavior. GeckoLib calls {@link #renderForBone} for every
 * bone after it has been positioned, so the pose stack is already in Torso-local space here.
 */
public class DragonBackGunLayer extends GeoRenderLayer<DragonEntity> {

    public DragonBackGunLayer(GeoRenderer<DragonEntity> renderer) {
        super(renderer);
    }

    @Override
    public void renderForBone(PoseStack poseStack, DragonEntity animatable, GeoBone bone, RenderType renderType,
                              MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick,
                              int packedLight, int packedOverlay) {
        if (!bone.getName().equals("Torso")) {
            return;
        }
        if (!Config.BACK_GUN_ENABLED.get()) {
            return;
        }
        if (GunRenderData.worldRenderDepth <= 0) {
            return;
        }

        Player player = animatable.getPlayer();
        if (player == null) {
            return;
        }
        DragonModelConfig.BackGunConfig backConfig = DragonModelConfig.backGunFor(player);
        if (!backConfig.enabled) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (player == mc.player && mc.options.getCameraType().isFirstPerson()) {
            return;
        }
        // While the dragon fades during ADS, a floating back gun would give the fade away;
        // hide it together with the ADS fade feature (no effect when the fade is disabled).
        if (shouldHideBackGun(animatable)) {
            return;
        }

        ItemStack stack = player == mc.player
                ? BackGunSync.findBackGunItem(player)
                : BackGunSync.clientBackGun(player.getId());
        if (stack.getItem() instanceof IGun) {
            renderBackGun(poseStack, bone, backConfig, stack, animatable, bufferSource, packedLight, packedOverlay);
        }
    }

    private static boolean shouldHideBackGun(DragonEntity animatable) {
        if (!Config.ADS_FEATURE_ENABLED.get()) {
            return false;
        }
        Player player = animatable.getPlayer();
        return player == Minecraft.getInstance().player
                && !Minecraft.getInstance().options.getCameraType().isFirstPerson()
                && IGunOperator.fromLivingEntity(player).getSynIsAiming();
    }

    private void renderBackGun(PoseStack poseStack, GeoBone bone, DragonModelConfig.BackGunConfig backConfig,
                               ItemStack stack, DragonEntity animatable,
                               MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        // renderForBone leaves the origin at the parent's pivot, so translate to the Torso
        // pivot first (see DragonSurvival's DragonBackpackRenderLayer), then apply the pose.
        poseStack.translate(
                bone.getPivotX() / 16f + backConfig.posX,
                bone.getPivotY() / 16f + backConfig.posY,
                bone.getPivotZ() / 16f + backConfig.posZ);
        poseStack.mulPose(Axis.ZP.rotationDegrees(backConfig.rotZ));
        poseStack.mulPose(Axis.YP.rotationDegrees(backConfig.rotY));
        poseStack.mulPose(Axis.XP.rotationDegrees(backConfig.rotX));
        float scale = backConfig.scale;
        poseStack.scale(scale, scale, scale);

        Minecraft.getInstance().getItemRenderer().renderStatic(
                stack, ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY,
                poseStack, bufferSource, animatable.level(), animatable.getId());
        poseStack.popPose();
    }
}
