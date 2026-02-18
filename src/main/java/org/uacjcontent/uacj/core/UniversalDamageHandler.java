package org.uacjcontent.uacj.core;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.uacjcontent.uacj.UacjConfig;

public class UniversalDamageHandler {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void handleUniversalDamage(LivingDamageEvent event) {
        DamageSource source = event.getSource();

        if (source.is(DamageTypes.THORNS) || source.isIndirect()) {
            if (!UacjConfig.enableThorns) return;
            float bonus = AmplificationHandler.getBonus(source.getEntity(), true);
            if (bonus > 0) event.setAmount(event.getAmount() * (1.0f + bonus));
            return;
        }

        if (source.getEntity() instanceof ServerPlayer attacker) {
            float bonus = AmplificationHandler.getBonus(attacker, false);
            if (bonus > 0) event.setAmount(event.getAmount() * (1.0f + bonus));
        }

        if (event.getEntity() instanceof ServerPlayer defender) {
            float bonus = AmplificationHandler.getBonus(defender, true);
            if (bonus > 0) event.setAmount(event.getAmount() * (1.0f - Math.min(bonus, 0.99f)));
        }
    }
}