package com.hoz.ds_tacz_compat.mixin;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.server.handlers.ServerFlightHandler;
import com.hoz.ds_tacz_compat.Config;
import com.tacz.guns.client.event.CameraSetupEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.neoforge.client.event.ViewportEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CameraSetupEvent.class, remap = false)
public class CameraSetupEventMixin {

    // TACZ kicks the camera via XRot/YRot during render; skip it for a dragon according to the
    // selected scope (gliding / any flight / always) so recoil doesn't shake the view.
    @Inject(method = "applyCameraRecoil", at = @At("HEAD"), cancellable = true, remap = false)
    private static void ds_tacz_compat$cancelDragonRecoil(ViewportEvent.ComputeCameraAngles event, CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !DragonStateProvider.isDragon(player) || !Config.RECOIL_CANCEL_ENABLED.get()) {
            return;
        }
        Config.RecoilCancelScope scope = Config.RECOIL_CANCEL_SCOPE.get();
        switch (scope) {
            case ALWAYS -> ci.cancel();
            case FLYING -> {
                if (ServerFlightHandler.isFlying(player)) {
                    ci.cancel();
                }
            }
            case GLIDING -> {
                if (ServerFlightHandler.isGliding(player)) {
                    ci.cancel();
                }
            }
        }
    }
}
