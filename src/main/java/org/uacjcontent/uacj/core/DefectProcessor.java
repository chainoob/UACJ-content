package org.uacjcontent.uacj.core;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.uacjcontent.uacj.UacjConfig;
import org.uacjcontent.uacj.api.PuffishHandler;
import org.uacjcontent.uacj.mixin.MobEffectInstanceAccessor;

public class DefectProcessor {
    private static final String REG = "uacj_effect_registry";
    private static final String EXP = "uacj_bonus_expiry";

    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added event) {
        final LivingEntity target = event.getEntity();
        if (target.level().isClientSide || !UacjConfig.enableDefects) return;

        final MobEffectInstance effect = event.getEffectInstance();
        final boolean isBeneficial = effect.getEffect().isBeneficial();

        final ServerPlayer primaryPlayer;

        if (isBeneficial) {
            if (!(target instanceof ServerPlayer player)) return;
            primaryPlayer = player;
        } else {
            if (!(event.getEffectSource() instanceof ServerPlayer attacker)) return;
            primaryPlayer = attacker;
        }

        final float bonus = AmplificationHandler.getBonus(primaryPlayer, isBeneficial);
        if (bonus <= 0.0f) return;

        final CompoundTag nbt = target.getPersistentData();
        if (!nbt.contains(REG)) nbt.put(REG, new CompoundTag());

        final CompoundTag registry = nbt.getCompound(REG);
        final int limit = PuffishHandler.getComplexityLimit(primaryPlayer);
        final String key = effect.getEffect().getDescriptionId();

        if (!registry.contains(key) && registry.size() >= limit) return;
        if (registry.getInt(key) > 0) return;

        final int newDur = (int) (effect.getDuration() * (1.0f + bonus));
        ((MobEffectInstanceAccessor) effect).setDuration(newDur);
        registry.putInt(key, newDur);
        nbt.putInt(EXP, 200);

        if (target instanceof ServerPlayer targetPlayer) {
            targetPlayer.connection.send(new ClientboundUpdateMobEffectPacket(target.getId(), effect));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onHurt(LivingDamageEvent event) {
        if (event.getSource().getEntity() instanceof net.minecraft.world.entity.projectile.ThrownPotion ||
                event.getSource().getEntity() instanceof net.minecraft.world.entity.AreaEffectCloud)
        {
            return;
        }

        if (!(event.getSource().getEntity() instanceof ServerPlayer attacker)) return;

        final float bonus = AmplificationHandler.getBonus(attacker, false);
        if (bonus <= 0.0f) return;

        final LivingEntity target = event.getEntity();
        final CompoundTag nbt = target.getPersistentData();
        if (!nbt.contains(REG)) nbt.put(REG, new CompoundTag());

        final CompoundTag registry = nbt.getCompound(REG);
        final int limit = PuffishHandler.getComplexityLimit(attacker);

        for (MobEffectInstance effect : target.getActiveEffects()) {
            if (effect.getEffect().isBeneficial()) continue;

            final String key = effect.getEffect().getDescriptionId();
            final int baseDuration = registry.getInt(key);
            final int targetDur;

            if (baseDuration == 0 && registry.size() < limit) {
                targetDur = (int) (effect.getDuration() * (1.0f + bonus));
                registry.putInt(key, targetDur);
            } else if (baseDuration > 0 && effect.getDuration() < baseDuration) {
                targetDur = baseDuration;
            } else {
                targetDur = 0;
            }

            if (targetDur > 0) {
                ((MobEffectInstanceAccessor) effect).setDuration(targetDur);
                nbt.putInt(EXP, 200);

                if (target instanceof ServerPlayer targetPlayer) {
                    targetPlayer.connection.send(new ClientboundUpdateMobEffectPacket(target.getId(), effect));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onTick(LivingEvent.LivingTickEvent event) {
        final LivingEntity target = event.getEntity();
        if (target.level().isClientSide || !UacjConfig.enableDefects) return;

        final CompoundTag nbt = target.getPersistentData();
        if (!nbt.contains(EXP)) return;

        final int expiry = nbt.getInt(EXP);
        if (expiry > 0) {
            nbt.putInt(EXP, expiry - 1);
        } else {
            nbt.remove(REG);
            nbt.remove(EXP);
        }
    }
}