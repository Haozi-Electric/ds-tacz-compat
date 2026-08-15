package com.hoz.ds_tacz_compat.mixin;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.server.handlers.ServerFlightHandler;
import com.hoz.ds_tacz_compat.ServerConfig;
import com.tacz.guns.entity.shooter.LivingEntityAim;
import com.tacz.guns.entity.shooter.ShooterDataHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LivingEntityAim.class, remap = false)
public abstract class LivingEntityAimMixin {

    @Shadow
    @Final
    private LivingEntity shooter;

    @Shadow
    @Final
    private ShooterDataHolder data;

    private boolean ds_tacz_compat$isGlidingDragon() {
        return ServerConfig.isGlidingShootingEnabled()
                && shooter instanceof Player player
                && DragonStateProvider.isDragon(player)
                && ServerFlightHandler.isGliding(player);
    }

    // tickSprint force-clears sprint on aim/reload; skip that for a gliding dragon so it stays in flight.
    @Redirect(method = "tickSprint", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setSprinting(Z)V"), remap = false)
    private void ds_tacz_compat$keepGlide(LivingEntity entity, boolean sprinting) {
        if (ds_tacz_compat$isGlidingDragon()) {
            return;
        }
        entity.setSprinting(sprinting);
    }

    // tickSprint accumulates sprintTimeS while sprinting (blocks shooting when > 0). The accumulation
    // happens inside a lambda, so zero it out after the method runs for a gliding dragon.
    @Inject(method = "tickSprint", at = @At("RETURN"), remap = false)
    private void ds_tacz_compat$zeroSprintTime(CallbackInfo ci) {
        if (ds_tacz_compat$isGlidingDragon()) {
            data.sprintTimeS = 0;
        }
    }
}
