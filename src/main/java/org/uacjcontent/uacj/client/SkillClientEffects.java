package org.uacjcontent.uacj.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.uacjcontent.uacj.core.SkillInteractionHandler;
import org.uacjcontent.uacj.network.NetworkHandler;
import org.uacjcontent.uacj.network.ExecuteInfusionPacket;

@Mod.EventBusSubscriber(modid = "uacj", value = Dist.CLIENT)
public class SkillClientEffects {
    private static int infusionTimer = 0;

    @SubscribeEvent
    public static void onClientTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !event.player.level().isClientSide()) return;

        final var mc = Minecraft.getInstance();
        final var player = mc.player;
        if (player == null) return;

        handleInfusionLogic(mc, player);
        handleSoulHarvestTrail(player);
        handleSnappyFlightPhysics(mc, player);
        handleSpectralParticles(player);
    }

    private static void handleSnappyFlightPhysics(Minecraft mc, Player player) {
        if (player.getPersistentData().getInt("uacj_spectral_state") == 2 && player.getAbilities().flying) {
            player.setDeltaMovement(Vec3.ZERO);

            float sideways = 0, upward = 0, forward = 0;
            if (mc.options.keyLeft.isDown()) sideways += 1;
            if (mc.options.keyRight.isDown()) sideways -= 1;
            if (mc.options.keyJump.isDown()) upward += 1;
            if (mc.options.keyShift.isDown()) upward -= 1;
            if (mc.options.keyUp.isDown()) forward += 1;
            if (mc.options.keyDown.isDown()) forward -= 1;

            player.travel(new Vec3(sideways, upward, forward));
            player.setDeltaMovement(player.getDeltaMovement().scale(7.0D)); 

            if (forward > 0) {
                Vec3 current = player.getDeltaMovement();
                double lookY = player.getLookAngle().y * (player.getAbilities().getFlyingSpeed() * 15.0);
                player.setDeltaMovement(current.x, current.y + lookY, current.z);
            }
        }
    }

    private static void handleSpectralParticles(Player player) {
        if (!player.getPersistentData().getBoolean("uacj_spectral_active")) return;

        if (player.getDeltaMovement().lengthSqr() > 0.001) {
            for (int i = 0; i < 2; i++) {
                player.level().addParticle(ParticleTypes.SOUL,
                        player.getX() + (player.getRandom().nextDouble() - 0.5) * 0.4,
                        player.getY() + 0.1,
                        player.getZ() + (player.getRandom().nextDouble() - 0.5) * 0.4,
                        0, 0.02, 0);
            }
        }
    }

    @SubscribeEvent
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        if (event.getEntity().getPersistentData().getBoolean("uacj_spectral_active")) {
            RenderSystem.setShaderColor(0.6F, 0.9F, 1.0F, 0.4F);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
        }
    }

    @SubscribeEvent
    public static void onRenderPlayerPost(RenderPlayerEvent.Post event) {
        if (event.getEntity().getPersistentData().getBoolean("uacj_spectral_active")) {
            RenderSystem.disableBlend();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    private static void handleInfusionLogic(Minecraft mc, Player player) {
        boolean isActive = player.getPersistentData().getBoolean("uacj_skill_infusion");
        boolean hasBook = SkillInteractionHandler.hasEnchantedBookInInventory(player);
        boolean isDown = mc.options.keyUse.isDown();

        if (isActive && hasBook && isDown) {
            infusionTimer++;
            if (infusionTimer % 2 == 0) spawnInfusionSpiral(player, infusionTimer);
            if (infusionTimer >= 100) {
                NetworkHandler.CHANNEL.sendToServer(new ExecuteInfusionPacket());
                infusionTimer = 0;
            }
        } else {
            infusionTimer = 0;
        }
    }

    private static void handleSoulHarvestTrail(Player player) {
        if (!player.getPersistentData().getBoolean("uacj_harvest_glow")) return;
        final Vec3 motion = player.getDeltaMovement();
        if (motion.lengthSqr() > 0.001 || player.swingTime > 0) {
            Vec3 look = player.getLookAngle();
            Vec3 right = look.cross(new Vec3(0, 1, 0)).normalize();
            double sideOffset = player.getMainArm() == HumanoidArm.RIGHT ? 0.5 : -0.5;
            final int density = 4;
            for (int i = 0; i < density; i++) {
                double lerp = i / (double) density;
                double x = (player.xo + (player.getX() - player.xo) * lerp) + (right.x * sideOffset) + (look.x * 0.3);
                double y = (player.yo + (player.getY() - player.yo) * lerp) + (player.getEyeHeight() * 0.7);
                double z = (player.zo + (player.getZ() - player.zo) * lerp) + (right.z * sideOffset) + (look.z * 0.3);
                player.level().addParticle(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, -motion.x * 0.1, 0.05, -motion.z * 0.1);
            }
        }
    }

    @SubscribeEvent
    public static void onFov(ComputeFovModifierEvent event) {
        if (infusionTimer > 0 && infusionTimer <= 100) {
            float progress = infusionTimer / 100.0f;
            event.setNewFovModifier(1.0f - (progress * 0.3f));
        }
    }

    private static void spawnInfusionSpiral(Player player, int timer) {
        double angle = timer * 0.4;
        player.level().addParticle(ParticleTypes.ENCHANT,
                player.getX() + Math.cos(angle) * 0.8,
                player.getY() + (timer / 100.0) * 2.0,
                player.getZ() + Math.sin(angle) * 0.8, 0, 0, 0);
    }
}