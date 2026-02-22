package org.uacjcontent.uacj.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerEntityMixin {
    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Player;noPhysics:Z", shift = At.Shift.AFTER))
    private void uacj$onTickAfterNoPhysics(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        if (player.getPersistentData().getInt("uacj_spectral_state") == 2) {
            player.noPhysics = true; // Phase through blocks
            player.fallDistance = 0; // Disable falling
            player.setOnGround(false); // Disable ground friction
        }
    }
}