package org.uacjcontent.uacj.core;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class EnchantInfusionHandler {

    public static void processInfusion(final ServerPlayer player) {
        final ItemStack mainHand = player.getMainHandItem();
        final boolean isEnchantable = mainHand.isEnchantable() || mainHand.isEnchanted();
        final Map<Enchantment, Integer> currentEnchants = EnchantmentHelper.getEnchantments(mainHand);

        final AtomicInteger totalXp = new AtomicInteger(0);
        final AtomicBoolean mainHandModified = new AtomicBoolean(false);
        final AtomicBoolean consumedAny = new AtomicBoolean(false);

        IntStream.range(0, player.getInventory().getContainerSize()).forEach(i -> {
            final ItemStack slotStack = player.getInventory().getItem(i);

            if (slotStack.is(Items.ENCHANTED_BOOK) && slotStack != mainHand) {
                final Map<Enchantment, Integer> bookEnchants = EnchantmentHelper.getEnchantments(slotStack);
                consumedAny.set(true);

                for (final Map.Entry<Enchantment, Integer> entry : bookEnchants.entrySet()) {
                    final Enchantment enchants = entry.getKey();
                    final int level = entry.getValue();

                    if (isEnchantable && enchants.canEnchant(mainHand) && EnchantmentHelper.isEnchantmentCompatible(currentEnchants.keySet(), enchants)) {
                        final int existingLevel = currentEnchants.getOrDefault(enchants, 0);

                        if (level > existingLevel) {
                            currentEnchants.put(enchants, level);
                            mainHandModified.set(true);
                        } else if (level == existingLevel && level < enchants.getMaxLevel()) {
                            currentEnchants.put(enchants, level + 1);
                            mainHandModified.set(true);
                        } else {
                            totalXp.addAndGet((enchants.getMinCost(level) * 2) * slotStack.getCount());
                        }
                    } else {
                        totalXp.addAndGet((enchants.getMinCost(level) * 2) * slotStack.getCount());
                    }
                }

                slotStack.shrink(slotStack.getCount());
            }
        });

        if (mainHandModified.get()) {
            EnchantmentHelper.setEnchantments(currentEnchants, mainHand);
        }

        if (totalXp.get() > 0) {
            player.giveExperiencePoints(totalXp.get());
        }

        if (consumedAny.get()) {
            finalizeSuccess(player);
        }
    }

    private static void finalizeSuccess(final ServerPlayer player) {
        player.getPersistentData().putBoolean("uacj_skill_infusion", false);
    }
}