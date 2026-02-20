package org.uacjcontent.uacj.mixin;

import dev.shadowsoffire.apotheosis.ench.table.ApothEnchantmentMenu;
import dev.shadowsoffire.apotheosis.ench.table.ApothEnchantmentMenu.TableStats;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.uacjcontent.uacj.init.AttributeInit;

@Mixin(value = ApothEnchantmentMenu.class, remap = false)
public abstract class ApothEnchantmentMixin {

    @Inject(method = "gatherStats(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;I)Ldev/shadowsoffire/apotheosis/ench/table/ApothEnchantmentMenu$TableStats;", at = @At("RETURN"), cancellable = true)
    private static void uacj$boostEnchantStats(Level level, BlockPos pos, int itemEnch, CallbackInfoReturnable<TableStats> cir) {
        TableStats originalStats = cir.getReturnValue();

        Player player = level.getNearestPlayer(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 8.0, false);

        if (player == null) return;

        var attr = player.getAttribute(AttributeInit.ENCHANT_DISCOUNT.get());
        if (attr != null && attr.getValue() > 0) {
            TableStats boostedStats = uacj_content$getBoostedStats(attr, originalStats);

            cir.setReturnValue(boostedStats);
        }
    }

    @Unique
    private static @NotNull TableStats uacj_content$getBoostedStats(AttributeInstance attr, TableStats originalStats) {
        float multiplier = (float) (1.0 + attr.getValue());

        return new TableStats(
                originalStats.eterna() * multiplier,
                originalStats.quanta() * multiplier,
                originalStats.arcana() * multiplier,
                originalStats.rectification(),
                originalStats.clues(),
                originalStats.blacklist(),
                originalStats.treasure()
        );
    }
}