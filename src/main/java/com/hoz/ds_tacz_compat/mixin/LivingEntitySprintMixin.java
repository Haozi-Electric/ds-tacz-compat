package com.hoz.ds_tacz_compat.mixin;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.server.handlers.ServerFlightHandler;
import com.tacz.guns.entity.shooter.LivingEntitySprint;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntitySprint.class, remap = false)
public abstract class LivingEntitySprintMixin {

    @Shadow
    @Final
    private LivingEntity shooter;

    // DS glide relies on isSprinting, while TACZ force-clears it on aim/reload. For a gliding
    // dragon, keep the raw sprint flag so aim doesn't kick it out of flight.
    @Inject(method = "getProcessedSprintStatus", at = @At("HEAD"), cancellable = true, remap = false)
    private void ds_tacz_compat$keepGlideSprint(boolean sprint, CallbackInfoReturnable<Boolean> cir) {
        if (shooter instanceof Player player
                && DragonStateProvider.isDragon(player)
                && ServerFlightHandler.isGliding(player)) {
            cir.setReturnValue(sprint);
        }
    }
}
