package com.hoz.ds_tacz_compat.mixin;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import com.hoz.ds_tacz_compat.Config;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tacz.guns.util.RenderHelper;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderHelper.class, remap = false)
public class RenderHelperMixin {

    @Inject(method = "renderFirstPersonArm", at = @At("HEAD"), cancellable = true, remap = false)
    private static void ds_tacz_compat$cancelDragonFirstPersonArm(LocalPlayer player, HumanoidArm hand,
                                                                  PoseStack matrixStack, int combinedLight,
                                                                  CallbackInfo ci) {
        if (!Config.HIDE_FIRST_PERSON_ARMS.get()) {
            return;
        }
        if (DragonStateProvider.isDragon(player)) {
            ci.cancel();
        }
    }
}
