package com.hoz.ds_tacz_compat.mixin;

import com.hoz.ds_tacz_compat.Config;
import com.tacz.guns.client.event.RenderCrosshairEvent;
import net.minecraft.client.CameraType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = RenderCrosshairEvent.class, remap = false)
public class RenderCrosshairEventMixin {

    // TACZ skips rendering its crosshair when not in first person (unless shoulder-surfing).
    // Only FORCE_SHOW_CROSSHAIR may force third-person rendering; the ADS feature must not
    // bypass this gate, otherwise a non-aiming third-person dragon still gets a crosshair.
    @Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/CameraType;isFirstPerson()Z"), remap = false)
    private static boolean ds_tacz_compat$forceShowCrosshair(CameraType cameraType) {
        if (Config.FORCE_SHOW_CROSSHAIR.get()) {
            return true;
        }
        return cameraType.isFirstPerson();
    }
}
