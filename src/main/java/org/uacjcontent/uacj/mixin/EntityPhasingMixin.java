package org.uacjcontent.uacj.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.uacjcontent.uacj.core.SpectralDataHandler;

@Mixin(Entity.class)
public abstract class EntityPhasingMixin {
    @Inject(method = "isPickable", at = @At("HEAD"))
    private void uacj$onIsPickable(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof Player player) {
            SpectralDataHandler.isSpectral(player);
        }
    }

    @Inject(method = "isPushable", at = @At("HEAD"))
    private void uacj$onIsPushable(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof Player player) {
            SpectralDataHandler.isSpectral(player);
        }
    }
}