package org.uacjcontent.uacj.core;

import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.uacjcontent.uacj.init.AttributeInit;
import org.uacjcontent.uacj.mixin.MobEffectInstanceAccessor;

import java.util.stream.StreamSupport;

@Mod.EventBusSubscriber(modid = "uacj")
public class CombatHandler {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void handleCombatModifications(LivingHurtEvent event) {
        final float initialAmount = event.getAmount();

        final float offenseMultiplier = calculateOffenseMultiplier(event);
        final float amountAfterOffense = initialAmount * offenseMultiplier;

        final float finalAmount = calculateFinalDefendedAmount(event, amountAfterOffense);

        if (finalAmount != initialAmount) {
            event.setAmount(finalAmount);
        }
    }

    private static float calculateOffenseMultiplier(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player attacker)) {
            return 1.0f;
        }

        final ItemStack weapon = attacker.getMainHandItem();
        final boolean isArrow = event.getSource().getDirectEntity() instanceof AbstractArrow;

        if (weapon.isEmpty() || !weapon.isEnchanted()) {
            return 1.0f;
        }

        if (isArrow && !(weapon.getItem() instanceof ProjectileWeaponItem)) {
            return 1.0f;
        }

        final var damageAttr = attacker.getAttribute(AttributeInit.ENCHANTED_WEAPON_DAMAGE.get());
        return (damageAttr != null && damageAttr.getValue() > 0)
                ? 1.0f + (float) damageAttr.getValue()
                : 1.0f;
    }

    private static float calculateFinalDefendedAmount(LivingHurtEvent event, float currentAmount) {
        if (!(event.getEntity() instanceof Player victim)) {
            return currentAmount;
        }

        float amount = currentAmount;

        if (event.getSource().is(DamageTypes.MAGIC) || event.getSource().is(DamageTypes.INDIRECT_MAGIC)) {
            final var magicAttr = victim.getAttribute(AttributeInit.MAGIC_RESISTANCE.get());
            if (magicAttr != null && magicAttr.getValue() > 0) {
                amount *= (1.0f - (float) Math.min(magicAttr.getValue(), 0.95));
            }
        }

        if (hasEnchantedArmor(victim)) {
            final var armorAttr = victim.getAttribute(AttributeInit.ENCHANTED_ARMOR_BONUS.get());
            if (armorAttr != null && armorAttr.getValue() > 0) {
                int enchantedCount = 0;
                for (ItemStack stack : victim.getArmorSlots()) {
                    if (!stack.isEmpty() && stack.isEnchanted()) enchantedCount++;
                }

                final float simulatedArmor = (float) (victim.getArmorValue() + (armorAttr.getValue() * enchantedCount));
                final float toughness = (float) victim.getAttributeValue(Attributes.ARMOR_TOUGHNESS);

                float reduction = Math.min(20.0f, Math.max(simulatedArmor / 5.0f, simulatedArmor - amount / (2.0f + toughness / 4.0f)));
                amount *= (1.0f - (reduction / 25.0f));
            }
        }

        return amount;
    }

    @SubscribeEvent
    public static void handleStatusEffects(MobEffectEvent.Added event) {
        if (event.getEntity() instanceof Player player && hasEnchantedArmor(player)) {
            MobEffectInstance effect = event.getEffectInstance();

            if (!effect.getEffect().isBeneficial()) {
                var resAttr = player.getAttribute(AttributeInit.MAGIC_RESISTANCE.get());

                if (resAttr != null && resAttr.getValue() > 0) {
                    int originalDur = effect.getDuration();
                    int targetDur = Math.max(20, (int) (originalDur * (1.0f - resAttr.getValue())));

                    ((MobEffectInstanceAccessor) effect).setDuration(targetDur);
                }
            }
        }
    }

    private static boolean hasEnchantedArmor(Player player) {
        return StreamSupport.stream(player.getArmorSlots().spliterator(), false)
                .anyMatch(stack -> !stack.isEmpty() && stack.isEnchanted());
    }
}