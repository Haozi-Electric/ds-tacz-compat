package com.hoz.ds_tacz_compat.mixin;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import com.hoz.ds_tacz_compat.Config;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.client.resource.GunDisplayInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GunDisplayInstance.class, remap = false)
public class GunDisplayInstanceMixin {

    // TACZ hides the crosshair once aiming progress passes 0.9. Pretend the gun forces the
    // crosshair so it stays up during third-person ADS.
    @Inject(method = "isShowCrosshair", at = @At("HEAD"), cancellable = true, remap = false)
    private void ds_tacz_compat$forceAimCrosshair(CallbackInfoReturnable<Boolean> cir) {
        if (shouldForceAimCrosshair()) {
            cir.setReturnValue(true);
        }
    }

    private static boolean shouldForceAimCrosshair() {
        if (!Config.ADS_FEATURE_ENABLED.get()) {
            return false;
        }
        LocalPlayer player = Minecraft.getInstance().player;
        return player != null
                && DragonStateProvider.isDragon(player)
                && !Minecraft.getInstance().options.getCameraType().isFirstPerson()
                && IGunOperator.fromLivingEntity(player).getSynIsAiming();
    }
}
