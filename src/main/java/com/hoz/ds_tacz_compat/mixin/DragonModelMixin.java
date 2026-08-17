package com.hoz.ds_tacz_compat.mixin;

import by.dragonsurvivalteam.dragonsurvival.client.models.DragonModel;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import com.hoz.ds_tacz_compat.Config;
import com.tacz.guns.api.entity.IGunOperator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DragonModel.class, remap = false)
public class DragonModelMixin {

    // entityCutout ignores alpha; switch to a translucent render type that writes no depth while
    // the local dragon ADS-aims in third person, so the faded body doesn't occlude anything
    // (the crosshair and entities behind stay visible). Note: 1.21 doesn't sort entity passes,
    // so some render-order artifacts with other entities are unavoidable.
    @Inject(method = "getRenderType", at = @At("HEAD"), cancellable = true, remap = false)
    private void ds_tacz_compat$fadeAimRenderType(DragonEntity animatable, ResourceLocation texture, CallbackInfoReturnable<RenderType> cir) {
        if (shouldFadeAimingDragon(animatable)) {
            cir.setReturnValue(RenderType.entityNoOutline(texture));
        }
    }

    private static boolean shouldFadeAimingDragon(DragonEntity animatable) {
        if (!Config.ADS_FEATURE_ENABLED.get()) {
            return false;
        }
        Player player = animatable.getPlayer();
        return player == Minecraft.getInstance().player
                && !Minecraft.getInstance().options.getCameraType().isFirstPerson()
                && IGunOperator.fromLivingEntity(player).getSynIsAiming();
    }
}
