package com.hoz.ds_tacz_compat.mixin;

import com.hoz.ds_tacz_compat.GunRenderData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * One-way compat with the Ayame PaperDoll DS Compat fork. Marks the paper doll
 * render so the gun renderers draw the floating/back gun in the HUD preview just
 * like third person. {@link Pseudo} makes this mixin silently skip when that fork
 * is not installed, so installing this mod alone changes nothing.
 */
@Pseudo
@Mixin(targets = "org.ayamemc.ayamepaperdoll.hud.PaperDollRenderer", remap = false)
public abstract class PaperDollRendererMixin {

    @Inject(method = "render", at = @At("HEAD"), remap = false)
    private void ds_tacz_compat$onPaperDollRenderHead(final CallbackInfo ci) {
        GunRenderData.paperDollRenderDepth++;
    }

    @Inject(method = "render", at = @At("RETURN"), remap = false)
    private void ds_tacz_compat$onPaperDollRenderReturn(final CallbackInfo ci) {
        if (GunRenderData.paperDollRenderDepth > 0) {
            GunRenderData.paperDollRenderDepth--;
        }
    }
}
