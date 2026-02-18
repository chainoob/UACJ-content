package org.uacjcontent.uacj.core;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.uacjcontent.uacj.UacjConfig;

import java.lang.reflect.Field;
import java.util.stream.StreamSupport;

public class DefectProcessor {
    private static final String NBT_BONUS = "uacj_active_bonus";
    private static final String NBT_EXPIRY = "uacj_bonus_expiry";
    private static final String NBT_REGISTRY = "uacj_effect_registry";
    private static final Field DURATION_FIELD = ObfuscationReflectionHelper.findField(MobEffectInstance.class, "f_19503_");

    public static float getBonus(Entity source, boolean armorCheck) {
        if (!(source instanceof ServerPlayer player)) return 0.0f;

        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();

        boolean enchanted = armorCheck ?
                StreamSupport.stream(player.getArmorSlots().spliterator(), false)
                        .anyMatch(s -> !s.isEmpty() && !EnchantmentHelper.getEnchantments(s).isEmpty()) :
                (!main.isEmpty() && !EnchantmentHelper.getEnchantments(main).isEmpty()) ||
                        (!off.isEmpty() && !EnchantmentHelper.getEnchantments(off).isEmpty());

        if (!enchanted) return 0.0f;
        var attr = player.getAttribute(EnchantAmplificationAttribute.AMP.get());
        return attr != null ? (float) Math.min(attr.getValue(), UacjConfig.globalCap) : 0.0f;
    }

    private static void setDurationDirectly(MobEffectInstance instance, int newDuration) throws IllegalAccessException {
        DURATION_FIELD.set(instance, newDuration);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void handleDamage(LivingDamageEvent event) throws IllegalAccessException {
        DamageSource source = event.getSource();
        Entity attacker = source.getEntity();
        LivingEntity victim = event.getEntity();

        if (source.is(DamageTypes.THORNS) || source.isIndirect()) {
            if (!UacjConfig.enableThorns) return;
            float b = getBonus(attacker, true);
            if (b > 0) event.setAmount(event.getAmount() * (1.0f + b));
            return;
        }

        if (attacker instanceof ServerPlayer player) {
            float b = getBonus(player, false);
            if (b > 0) {
                event.setAmount(event.getAmount() * (1.0f + b));
                processEffects(victim, b, false);
                processEffects(player, b, true);
            }
        }

        if (victim instanceof ServerPlayer defender) {
            float b = getBonus(defender, true);
            if (b > 0) {
                event.setAmount(event.getAmount() * (1.0f - b));
                processEffects(defender, b, true);
            }
        }
    }

    private static void processEffects(LivingEntity entity, float bonus, boolean beneficialOnly) throws IllegalAccessException {
        if (!UacjConfig.enableDefects) return;
        if (beneficialOnly && !(entity instanceof ServerPlayer)) return;

        CompoundTag nbt = entity.getPersistentData();
        if (!nbt.contains(NBT_REGISTRY)) nbt.put(NBT_REGISTRY, new CompoundTag());
        CompoundTag registry = nbt.getCompound(NBT_REGISTRY);

        for (MobEffectInstance effect : entity.getActiveEffects()) {
            if (beneficialOnly != effect.getEffect().isBeneficial()) continue;

            String key = effect.getEffect().getDescriptionId();
            int current = effect.getDuration();
            int baseline = registry.getInt(key);

            if (current > 0 && current < 32000 && baseline == 0) {
                int target = (int) (current * (1.0f + bonus));
                setDurationDirectly(effect, target);
                registry.putInt(key, target);
            }
            else if (current > 0 && current < baseline) {
                setDurationDirectly(effect, baseline);
            }
        }

        int statusTicks = Math.max(entity.getRemainingFireTicks(), entity.getTicksFrozen()) + 120;
        nbt.putFloat(NBT_BONUS, bonus);
        nbt.putInt(NBT_EXPIRY, statusTicks);
    }

    @SubscribeEvent
    public static void handleStatusDuration(LivingEvent.LivingTickEvent event) {
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide || !UacjConfig.enableDefects) return;

        CompoundTag nbt = target.getPersistentData();
        float bonus = nbt.getFloat(NBT_BONUS);
        int expiry = nbt.getInt(NBT_EXPIRY);

        if (bonus <= 0) return;

        if (target.tickCount % 7 == 0) {
            int fire = target.getRemainingFireTicks();
            int freeze = target.getTicksFrozen();
            if (fire > 0) target.setRemainingFireTicks(fire + 1);
            if (freeze > 0) target.setTicksFrozen(freeze + 1);
        }

        if (expiry > 0) {
            nbt.putInt(NBT_EXPIRY, expiry - 1);
        } else {
            nbt.remove(NBT_BONUS);
            nbt.remove(NBT_EXPIRY);
            nbt.remove(NBT_REGISTRY);
        }
    }
}