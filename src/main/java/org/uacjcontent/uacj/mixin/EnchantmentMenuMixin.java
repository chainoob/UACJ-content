package org.uacjcontent.uacj.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.EnchantmentMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.uacjcontent.uacj.init.AttributeInit;

@Mixin(EnchantmentMenu.class)
public abstract class EnchantmentMenuMixin {

    @Shadow @Final private ContainerLevelAccess access;
    @Shadow @Final public int[] costs;

    @Inject(method = "slotsChanged", at = @At("TAIL"))
    private void uacj$applyDiscountAfterUpdate(Container container, CallbackInfo ci) {
        this.access.execute((level, pos) -> {
            Player player = level.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 4.0, false);

            if (player != null) {
                var attr = player.getAttribute(AttributeInit.ENCHANT_DISCOUNT.get());
                if (attr != null && attr.getValue() > 0) {
                    double modifier = 1.0 - attr.getValue();

                    for (int i = 0; i < this.costs.length; i++) {
                        if (this.costs[i] > 0) {
                            this.costs[i] = Math.max(1, (int) Math.floor(this.costs[i] * modifier));
                        }
                    }
                }
            }
        });
    }
}