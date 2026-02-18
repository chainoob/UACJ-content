package org.uacjcontent.uacj.core;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.uacjcontent.uacj.UacjConfig;
import org.uacjcontent.uacj.init.AttributeInit;
import java.util.stream.StreamSupport;

public class AmplificationHandler {
    public static float getBonus(Entity source, boolean armorOnly) {
        if (!(source instanceof ServerPlayer player)) return 0.0f;

        boolean enchanted = armorOnly ?
                StreamSupport.stream(player.getArmorSlots().spliterator(), false)
                        .anyMatch(s -> !s.isEmpty() && !EnchantmentHelper.getEnchantments(s).isEmpty()) :
                StreamSupport.stream(player.getHandSlots().spliterator(), false)
                        .anyMatch(s -> !s.isEmpty() && !EnchantmentHelper.getEnchantments(s).isEmpty());

        if (!enchanted) return 0.0f;
        var attr = player.getAttribute(AttributeInit.AMP.get());
        return attr != null ? (float) Math.min(attr.getValue(), UacjConfig.globalCap) : 0.0f;
    }
}
