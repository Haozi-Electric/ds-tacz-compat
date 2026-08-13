package com.hoz.ds_tacz_compat.mixin;

import by.dragonsurvivalteam.dragonsurvival.client.render.entity.dragon.DragonRenderer;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import com.hoz.ds_tacz_compat.GunRenderData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.cache.object.BakedGeoModel;

@Mixin(value = DragonRenderer.class, remap = false)
public class DragonRendererMixin {

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
}
