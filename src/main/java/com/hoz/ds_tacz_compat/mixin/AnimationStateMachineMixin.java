package com.hoz.ds_tacz_compat.mixin;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.server.handlers.ServerFlightHandler;
import com.tacz.guns.api.client.animation.statemachine.AnimationStateMachine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = AnimationStateMachine.class, remap = false)
public abstract class AnimationStateMachineMixin {

    // A gliding dragon reports isSprinting=true (DS glide uses the sprint flag), which makes
    // TACZ's tick logic trigger the "run" animation input. Rewrite it to "walk" so the gun
    // keeps the normal held pose instead of the holster/run pose.
    @ModifyVariable(method = "trigger", at = @At("HEAD"), argsOnly = true, remap = false)
    private String ds_tacz_compat$runToWalk(String condition) {
        if ("run".equals(condition)) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null && DragonStateProvider.isDragon(player) && ServerFlightHandler.isGliding(player)) {
                return "walk";
            }
        }
        return condition;
    }
}
