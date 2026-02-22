package org.uacjcontent.uacj.core;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent;
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
        if (player.getPersistentData().getBoolean("uacj_skill_infusion") &&
                SkillInteractionHandler.hasEnchantedBookInInventory(player)) {
            event.setCanceled(true);
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void onEntityKilled(final LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayer player) {
            if (player.getPersistentData().getBoolean("uacj_skill_soul_harvest")) {
                SoulHarvestProcessor.executeHarvest(player, event.getEntity());
            }
        }
    }

    @SubscribeEvent
    public static void onAttack(final LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayer player) {
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
        if (event.getEntity().level().isClientSide()) return;

        if (UacjConfig.enableDefects && event.getSource().getEntity() instanceof ServerPlayer attacker) {
            if (!(event.getSource().getDirectEntity() instanceof ThrownPotion)) {
                DefectProcessor.processHurt(event, attacker);
            }
        }

        UniversalDamageHandler.handleUniversalDamage(event);
    }

    @SubscribeEvent
    public static void onLivingTick(final LivingEvent.LivingTickEvent event) {
        final LivingEntity entity = event.getEntity();
        if (!entity.level().isClientSide()) {
            if (UacjConfig.enableDefects) {
                DefectProcessor.processTick(entity);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (SpectralDataHandler.isSpectral(player)) {
            player.getAbilities().mayfly = true;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            SpectralFlightProcessor.serverTick(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        if (SpectralDataHandler.isSpectral(event.getEntity())) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor(0.5F, 0.8F, 1.0F, 0.35F);
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
        }
    }

    @SubscribeEvent
    public static void onRenderPlayerPost(RenderPlayerEvent.Post event) {
        if (SpectralDataHandler.isSpectral(event.getEntity())) {
            RenderSystem.disableBlend();

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}