package org.uacjcontent.uacj.core;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.uacjcontent.uacj.UacjConfig;
import org.uacjcontent.uacj.api.PuffishHandler;
import org.uacjcontent.uacj.util.ReflectionHelper;

public class DefectProcessor {
    private static final String REG = "uacj_effect_registry";
    private static final String EXP = "uacj_bonus_expiry";

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onHurt(LivingDamageEvent event) throws IllegalAccessException {
        if (event.getSource().getEntity() instanceof ServerPlayer attacker) {
            final float bonus = AmplificationHandler.getBonus(attacker, false);
            if (bonus > 0) process(event.getEntity(), attacker, bonus, false, true);
        }
    }

    @SubscribeEvent
    public static void onTick(LivingEvent.LivingTickEvent event) throws IllegalAccessException {
        final LivingEntity target = event.getEntity();
        if (target.level().isClientSide || !UacjConfig.enableDefects) return;

        if (target instanceof ServerPlayer player) {
            final float bonus = AmplificationHandler.getBonus(player, true);
            if (bonus > 0) process(player, player, bonus, true, false);
        }

        final CompoundTag nbt = target.getPersistentData();
        final int expiry = nbt.getInt(EXP);
        if (expiry > 0) {
            nbt.putInt(EXP, expiry - 1);
        } else if (nbt.contains(REG)) {
            nbt.remove(REG);
        }
    }

    private static void process(LivingEntity target, ServerPlayer player, float bonus, boolean beneficial, boolean isRefresh) throws IllegalAccessException {
        CompoundTag nbt = target.getPersistentData();
        if (!nbt.contains(REG)) nbt.put(REG, new CompoundTag());

        CompoundTag registry = nbt.getCompound(REG);
        int limit = PuffishHandler.getComplexityLimit(player);

        for (MobEffectInstance effect : target.getActiveEffects()) {
            if (beneficial != effect.getEffect().isBeneficial()) continue;

            String key = effect.getEffect().getDescriptionId();
            int baseDuration = registry.getInt(key);

            if (baseDuration == 0 && registry.size() < limit) {
                int newDur = (int) (effect.getDuration() * (1.0f + bonus));
                ReflectionHelper.setDuration(effect, newDur);
                registry.putInt(key, newDur);
            }

            else if (isRefresh && effect.getDuration() < 10 && baseDuration > 0) {
                ReflectionHelper.setDuration(effect, baseDuration);
            }
        }
        nbt.putInt(EXP, 200);
    }
}