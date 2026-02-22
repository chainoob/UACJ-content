package org.uacjcontent.uacj.core;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.uacjcontent.uacj.UacjConfig;

@Mod.EventBusSubscriber(modid = "uacj")
public class GlobalEventHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickItem(final PlayerInteractEvent.RightClickItem event) {
        handleInfusionIntercept(event);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickBlock(final PlayerInteractEvent.RightClickBlock event) {
        handleInfusionIntercept(event);
    }

    private static void handleInfusionIntercept(final PlayerInteractEvent event) {
        final Player player = event.getEntity();
        if (player.getPersistentData().getBoolean("uacj_skill_infusion_active") &&
                SkillInteractionHandler.hasEnchantedBookInInventory(player)) {
            event.setCanceled(true);
            event.setResult(Event.Result.DENY);
        }
    }


    @SubscribeEvent
    public static void onEntityKilled(final LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            if (player.getPersistentData().getBoolean("uacj_skill_soul_harvest_active")) {
                SoulHarvestProcessor.executeHarvest(player, event.getEntity());
            }
        }
    }

    @SubscribeEvent
    public static void onAttack(final LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            SoulHarvestProcessor.purgeBonus(player);
        }
    }


    @SubscribeEvent
    public static void onEffectAdded(final MobEffectEvent.Added event) {
        if (!event.getEntity().level().isClientSide() && UacjConfig.enableDefects) {
            DefectProcessor.processEffectAddition(event);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onDamage(final LivingDamageEvent event) {
        if (UacjConfig.enableDefects && event.getSource().getEntity() instanceof ServerPlayer attacker) {
            if (!(event.getSource().getDirectEntity() instanceof net.minecraft.world.entity.projectile.ThrownPotion)) {
                DefectProcessor.processHurt(event, attacker);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingTick(final LivingEvent.LivingTickEvent event) {
        final var entity = event.getEntity();
        if (!entity.level().isClientSide()) {
            if (UacjConfig.enableDefects) {
                DefectProcessor.processTick(entity);
            }
        }
    }
}