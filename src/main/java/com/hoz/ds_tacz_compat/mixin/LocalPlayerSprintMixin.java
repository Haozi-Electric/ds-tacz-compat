package com.hoz.ds_tacz_compat.mixin;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.server.handlers.ServerFlightHandler;
import com.hoz.ds_tacz_compat.ServerConfig;
import com.tacz.guns.client.gameplay.LocalPlayerSprint;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LocalPlayerSprint.class, remap = false)
public abstract class LocalPlayerSprintMixin {

    @Shadow
    @Final
    private LocalPlayer player;

    @Inject(method = "getProcessedSprintStatus", at = @At("HEAD"), cancellable = true, remap = false)
    private void ds_tacz_compat$keepGlideSprint(boolean sprint, CallbackInfoReturnable<Boolean> cir) {
        if (!ServerConfig.isGlidingShootingEnabled()) {
            return;
        }
        if (DragonStateProvider.isDragon(player)
                && ServerFlightHandler.isGliding(player)) {
            cir.setReturnValue(sprint);
        }
    }
}
