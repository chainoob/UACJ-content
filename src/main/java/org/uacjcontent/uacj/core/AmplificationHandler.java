package org.uacjcontent.uacj.core;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.uacjcontent.uacj.UacjConfig;
import org.uacjcontent.uacj.init.AttributeInit;

public class AmplificationHandler {

    public static float getBonus(Entity source, boolean armorOnly) {
        if (!(source instanceof ServerPlayer player)) return 0.0f;

        var attr = player.getAttribute(AttributeInit.AMP.get());
        if (attr == null || attr.getValue() <= 0) return 0.0f;

        Iterable<ItemStack> slots = armorOnly ? player.getArmorSlots() : player.getHandSlots();

        for (ItemStack stack : slots) {
            if (!stack.isEmpty() && !EnchantmentHelper.getEnchantments(stack).isEmpty()) {
                return (float) Math.min(attr.getValue(), UacjConfig.globalCap);
            }
        }

        return 0.0f;
    }
}