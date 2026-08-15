package com.hoz.ds_tacz_compat.mixin;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import com.hoz.ds_tacz_compat.ServerConfig;
import com.tacz.guns.entity.shooter.LivingEntitySpeedModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = LivingEntitySpeedModifier.class, remap = false)
public abstract class LivingEntitySpeedModifierMixin {

    @Shadow
    @Final
    private LivingEntity shooter;

    // Scale the weight + aim/reload/base speed modifiers for dragons so servers can
    // tune how much TACZ slows them down (0 = none, 1 = vanilla, 2 = double).
    @Redirect(method = "updateSpeedModifier", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/AttributeInstance;addTransientModifier(Lnet/minecraft/world/entity/ai/attributes/AttributeModifier;)V"), remap = false)
    private void ds_tacz_compat$scaleSpeed(AttributeInstance instance, AttributeModifier modifier) {
        if (shooter instanceof Player player && DragonStateProvider.isDragon(player)) {
            double factor = ServerConfig.speedInfluenceMultiplier();
            modifier = new AttributeModifier(modifier.id(), modifier.amount() * factor, modifier.operation());
        }
        instance.addTransientModifier(modifier);
    }
}
