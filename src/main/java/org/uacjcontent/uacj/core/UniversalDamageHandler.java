package org.uacjcontent.uacj.core;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class UniversalDamageHandler {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onFinalDamage(LivingDamageEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayer attacker) {
            float bonus = DefectProcessor.getBonus(attacker, false);
            if (bonus > 0) {
                event.setAmount(event.getAmount() * (1.0f + bonus));
            }
        }

        if (event.getEntity() instanceof ServerPlayer defender) {
            float bonus = DefectProcessor.getBonus(defender, true);
            if (bonus > 0) {
                event.setAmount(event.getAmount() * (1.0f - bonus));
            }
        }
    }
}