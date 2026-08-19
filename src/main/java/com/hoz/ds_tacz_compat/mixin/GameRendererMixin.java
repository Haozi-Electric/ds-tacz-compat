package com.hoz.ds_tacz_compat.mixin;

import com.hoz.ds_tacz_compat.RefitAnimHelper;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tacz.guns.compat.iris.IrisCompat;
import net.minecraft.client.CameraType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = GameRenderer.class, remap = false)
public abstract class GameRendererMixin {

    // renderItemInHand runs for third person too; only its isFirstPerson() gate blocks
    // rendering the held item. Let a dragon opening the refit screen pass the gate so the
    // TACZ first-person refit animation (refit_view locator + RefitTransform) takes over.
    @Redirect(method = "renderItemInHand",
              at = @At(value = "INVOKE", target = "Lnet/minecraft/client/CameraType;isFirstPerson()Z", ordinal = 0),
              remap = false)
    private static boolean ds_tacz_compat$dragonRefitView(CameraType type) {
        if (type.isFirstPerson()) {
            return true;
        }
        return RefitAnimHelper.isDragonRefitScreen();
    }

    // iris @Redirects renderHandsWithItems away while a shader pack is active, breaking the
    // TACZ RenderHandEvent. Wrap the call instead: for a dragon opening the refit screen with a
    // shader pack active, run the vanilla call ourselves so the refit gun renders; otherwise
    // delegate to the original handler (vanilla or iris).
    @WrapOperation(method = "renderItemInHand",
                   at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderHandsWithItems(FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lnet/minecraft/client/player/LocalPlayer;I)V"),
                   remap = false)
    private static void ds_tacz_compat$renderHands(ItemInHandRenderer renderer, float partialTicks, PoseStack poseStack,
                                                   MultiBufferSource.BufferSource buffer, LocalPlayer player, int light,
                                                   Operation<Void> original) {
        if (RefitAnimHelper.isDragonRefitScreen() && IrisCompat.isPackInUseQuick()) {
            // iris leaves the depth state reversed (GREATER) after world rendering and renders
            // hands itself via HandRenderer; a manual call here would otherwise invert the
            // occlusion. Reset to Minecraft's standard LEQUAL so the refit gun occludes correctly.
            RenderSystem.enableDepthTest();
            RenderSystem.depthFunc(515); // GL_LEQUAL
            RenderSystem.depthMask(true);
            renderer.renderHandsWithItems(partialTicks, poseStack, buffer, player, light);
            return;
        }
        original.call(renderer, partialTicks, poseStack, buffer, player, light);
    }
}
