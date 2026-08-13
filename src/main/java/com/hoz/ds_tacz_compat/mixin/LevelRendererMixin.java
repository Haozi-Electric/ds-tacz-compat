package com.hoz.ds_tacz_compat.mixin;

import com.hoz.ds_tacz_compat.GunRenderData;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LevelRenderer.class, remap = false)
public abstract class LevelRendererMixin {

    @Inject(method = "renderLevel", at = @At("HEAD"), remap = false)
    private void ds_tacz_compat$worldRenderStart(CallbackInfo ci) {
        GunRenderData.worldRenderDepth++;
    }

    @Inject(method = "renderLevel", at = @At("RETURN"), remap = false)
    private void ds_tacz_compat$worldRenderEnd(CallbackInfo ci) {
        GunRenderData.worldRenderDepth--;
    }
}
