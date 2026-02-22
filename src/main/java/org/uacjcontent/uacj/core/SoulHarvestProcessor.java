package org.uacjcontent.uacj.core;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public final class SoulHarvestProcessor {
    private static final UUID SOUL_DAMAGE_UUID = UUID.fromString("7a1b2c3d-4e5f-6a7b-8c9d-0e1f2a3b4c5d");

    public static void handleKill(final Player player, final LivingEntity victim) {
        final int xp = Math.max(1, victim.getExperienceReward());

        final ItemStack mainHand = player.getMainHandItem();
        if (mainHand.isDamageableItem() && mainHand.isDamaged()) {
            mainHand.setDamageValue(Math.max(0, mainHand.getDamageValue() - xp));
        }

        final var damageAttr = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damageAttr != null) {
            damageAttr.removeModifier(SOUL_DAMAGE_UUID);
            damageAttr.addTransientModifier(new AttributeModifier(
                    SOUL_DAMAGE_UUID,
                    "Soul Harvest Bonus",
                    xp * 0.5,
                    AttributeModifier.Operation.ADDITION
            ));
        }

        // 3. Apply Speed II (5 seconds)
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 1));
    }

    public static void consumeBonus(final Player player) {
        final var damageAttr = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damageAttr != null && damageAttr.getModifier(SOUL_DAMAGE_UUID) != null) {
            damageAttr.removeModifier(SOUL_DAMAGE_UUID);
        }
    }
}
