package org.uacjcontent.uacj.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.uacjcontent.uacj.core.SkillInteractionHandler;
import org.uacjcontent.uacj.network.NetworkHandler;
import org.uacjcontent.uacj.util.ExecuteInfusionPacket;

@Mod.EventBusSubscriber(modid = "uacj", value = Dist.CLIENT)
public class SkillClientEffects {
    private static int holdTimer = 0;

    @SubscribeEvent
    public static void onClientTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !event.player.level().isClientSide()) return;

        var mc = Minecraft.getInstance();
        var player = mc.player;
        if (player == null) return;

        boolean isActive = player.getPersistentData().getBoolean("uacj_skill_infusion");
        boolean hasBook = SkillInteractionHandler.hasEnchantedBookInInventory(player);
        boolean isDown = mc.options.keyUse.isDown();

        if (isActive && hasBook && isDown) {
            holdTimer++;

            if (holdTimer % 2 == 0) {
                spawnClientParticles(player, holdTimer);
            }

            if (holdTimer >= 100) {
                NetworkHandler.CHANNEL.sendToServer(new ExecuteInfusionPacket());
                holdTimer = 0;
            }
        } else {
            holdTimer = 0;
        }
    }

    @SubscribeEvent
    public static void onComputeFov(ComputeFovModifierEvent event) {
        if (holdTimer > 0 && holdTimer <= 100) {
            float progress = holdTimer / 100.0f;
            event.setNewFovModifier(1.0f - (progress * 0.3f));
        }
    }

    private static void spawnClientParticles(net.minecraft.client.player.LocalPlayer player, int timer) {
        double angle = timer * 0.4;
        double radius = 0.8;
        double x = Math.cos(angle) * radius;
        double z = Math.sin(angle) * radius;
        double progress = timer / 100.0;
        double yOffset = progress * 2.0;

        player.level().addParticle(ParticleTypes.ENCHANT, player.getX() + x, player.getY() + yOffset, player.getZ() + z, 0, 0, 0);
    }
}