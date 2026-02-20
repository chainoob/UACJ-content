package org.uacjcontent.uacj.core;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import java.util.Map;

public class SkillLogicHandler {

    public static void processInfusion(ServerPlayer player) {
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        if (!offHand.is(Items.ENCHANTED_BOOK)) return;

        Map<Enchantment, Integer> bookEnchants = EnchantmentHelper.getEnchantments(offHand);
        boolean isEnchantable = mainHand.isEnchantable() || mainHand.isEnchanted();

        if (isEnchantable) {
            Map<Enchantment, Integer> currentEnchants = EnchantmentHelper.getEnchantments(mainHand);
            boolean applied = false;

            for (Map.Entry<Enchantment, Integer> entry : bookEnchants.entrySet()) {
                Enchantment ench = entry.getKey();
                if (ench.canEnchant(mainHand) && EnchantmentHelper.isEnchantmentCompatible(currentEnchants.keySet(), ench)) {
                    currentEnchants.put(ench, Math.max(currentEnchants.getOrDefault(ench, 0), entry.getValue()));
                    applied = true;
                }
            }

            if (applied) {
                EnchantmentHelper.setEnchantments(currentEnchants, mainHand);
                offHand.shrink(1);
                finalizeSuccess(player);
            }
        } else {
            int xp = bookEnchants.entrySet().stream()
                    .mapToInt(e -> e.getKey().getMinCost(e.getValue()))
                    .sum() * 2;
            player.giveExperiencePoints(xp);
            offHand.setCount(0);
            finalizeSuccess(player);
        }
    }

    private static void finalizeSuccess(ServerPlayer player) {
        player.getPersistentData().putBoolean("uacj_skill_infusion_active", false);
    }
}