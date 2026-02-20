package org.uacjcontent.uacj.core;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.uacjcontent.uacj.init.AttributeInit;

public class ProgressionHandler {

    @SubscribeEvent
    public static void onXpPickup(PlayerXpEvent.PickupXp event) {
        Player player = event.getEntity();
        var attr = player.getAttribute(AttributeInit.EXP_GAIN.get());

        if (attr != null && attr.getValue() > 1.0) {
            int originalValue = event.getOrb().getValue();
            int bonusValue = (int) (originalValue * (attr.getValue() - 1.0));
            event.getOrb().value = originalValue + bonusValue;
        }
    }

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;

        var attr = player.getAttribute(AttributeInit.ENCHANT_DISCOUNT.get());
        if (attr != null && attr.getValue() > 0) {
            int baseCost = event.getCost();
            double modifier = 1.0 - attr.getValue();
            event.setCost((int) Math.max(1, Math.floor(baseCost * modifier)));
        }
    }

    @SubscribeEvent
    public static void onEnchantLevelSet(EnchantmentLevelSetEvent event) {
        Player player = event.getLevel().getNearestPlayer(
                event.getPos().getX(),
                event.getPos().getY(),
                event.getPos().getZ(),
                8.0,
                false
        );

        if (player != null) {
            var attr = player.getAttribute(AttributeInit.ENCHANT_DISCOUNT.get());
            if (attr != null && attr.getValue() > 0) {
                int original = event.getEnchantLevel();
                double modifier = 1.0 - attr.getValue();

                int discounted = (int) Math.max(1, Math.floor(original * modifier));

                event.setEnchantLevel(discounted);
            }
        }
    }
}