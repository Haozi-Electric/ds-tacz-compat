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
import net.minecraft.world.entity.player.Inventory;
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

    // Calibrated back-gun pose on the Torso bone (position in blocks, rotation in degrees).
    private static final float OFFSET_X = 0.0f;
    private static final float OFFSET_Y = 0.3f;
    private static final float OFFSET_Z = 0.33f;
    private static final float ROT_X = 90.0f;
    private static final float ROT_Y = 120.0f;
    private static final float ROT_Z = 0.0f;

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
        if (GunRenderData.isBackGunModelDisabled(player)) {
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

        Inventory inventory = player.getInventory();
        if (inventory.selected == 0) {
            return;
        }
        ItemStack stack = inventory.getItem(0);
        if (stack.getItem() instanceof IGun) {
            renderBackGun(poseStack, bone, stack, animatable, bufferSource, packedLight, packedOverlay);
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

    private void renderBackGun(PoseStack poseStack, GeoBone bone, ItemStack stack, DragonEntity animatable,
                               MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        // renderForBone leaves the origin at the parent's pivot, so translate to the Torso
        // pivot first (see DragonSurvival's DragonBackpackRenderLayer), then apply the pose.
        poseStack.translate(
                bone.getPivotX() / 16f + OFFSET_X,
                bone.getPivotY() / 16f + OFFSET_Y,
                bone.getPivotZ() / 16f + OFFSET_Z);
        poseStack.mulPose(Axis.ZP.rotationDegrees(ROT_Z));
        poseStack.mulPose(Axis.YP.rotationDegrees(ROT_Y));
        poseStack.mulPose(Axis.XP.rotationDegrees(ROT_X));
        float scale = Config.BACK_GUN_SCALE.get().floatValue();
        poseStack.scale(scale, scale, scale);

        Minecraft.getInstance().getItemRenderer().renderStatic(
                stack, ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY,
                poseStack, bufferSource, animatable.level(), animatable.getId());
        poseStack.popPose();
    }
}
