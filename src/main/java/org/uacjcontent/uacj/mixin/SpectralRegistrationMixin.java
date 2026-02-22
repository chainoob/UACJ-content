package org.uacjcontent.uacj.mixin;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.uacjcontent.uacj.core.SpectralDataHandler;

@Mixin(Player.class)
public abstract class SpectralRegistrationMixin {
    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void uacj$registerSpectralData(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        player.getEntityData().define(SpectralDataHandler.SPECTRAL_STATE, 0);
    }
}