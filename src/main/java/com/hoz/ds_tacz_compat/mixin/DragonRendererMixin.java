package com.hoz.ds_tacz_compat.mixin;

import by.dragonsurvivalteam.dragonsurvival.client.render.entity.dragon.DragonRenderer;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import com.hoz.ds_tacz_compat.Config;
import com.hoz.ds_tacz_compat.DragonBackGunLayer;
import com.hoz.ds_tacz_compat.GunRenderData;
import com.tacz.guns.api.entity.IGunOperator;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.player.Player;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.util.Color;

@Mixin(value = DragonRenderer.class, remap = false)
public class DragonRendererMixin {

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void ds_tacz_compat$addBackGunLayer(EntityRendererProvider.Context context, GeoModel<DragonEntity> model, CallbackInfo ci) {
        DragonRenderer self = (DragonRenderer) (Object) this;
        self.getRenderLayers().add(new DragonBackGunLayer(self));
    }

    @Inject(method = "preRender", at = @At("TAIL"), remap = false)
    private void ds_tacz_compat$captureWorldMatrix(PoseStack poseStack, DragonEntity animatable,
                                                    BakedGeoModel model, MultiBufferSource bufferSource,
                                                    VertexConsumer buffer, boolean isReRender,
                                                    float partialTick, int packedLight, int packedOverlay,
                                                    int colour, CallbackInfo ci) {
        if (GunRenderData.worldRenderDepth <= 0) {
            return;
        }
        GunRenderData.worldMatrix = new Matrix4f(poseStack.last().pose());
    }

    // Scale the vertex alpha down while the local dragon ADS-aims in third person, so the
    // translucent model fades and no longer hides the crosshair.
    @Inject(method = "getRenderColor", at = @At("RETURN"), cancellable = true, remap = false)
    private void ds_tacz_compat$fadeAimColor(DragonEntity animatable, float partialTick, int packedLight, CallbackInfoReturnable<Color> cir) {
        if (shouldFadeAimingDragon(animatable)) {
            Color color = cir.getReturnValue();
            float alpha = 1.0f - Config.ADS_FADE_AMOUNT.get().floatValue();
            cir.setReturnValue(Color.ofRGBA(color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(), alpha));
        }
    }

    private static boolean shouldFadeAimingDragon(DragonEntity animatable) {
        if (!Config.ADS_FEATURE_ENABLED.get()) {
            return false;
        }
        Player player = animatable.getPlayer();
        return player == Minecraft.getInstance().player
                && !Minecraft.getInstance().options.getCameraType().isFirstPerson()
                && IGunOperator.fromLivingEntity(player).getSynIsAiming();
    }
}
