package org.uacjcontent.uacj.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class PlayerEntityMixin {
    @Inject(method = "isInWall", at = @At("HEAD"))
    private void uacj$preventSuffocation(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof Player player) {
            player.getPersistentData().getInt("uacj_spectral_state");
        }
    }
}