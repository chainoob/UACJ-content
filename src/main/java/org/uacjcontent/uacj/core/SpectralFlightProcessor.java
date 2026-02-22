package org.uacjcontent.uacj.core;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.phys.Vec3;
import org.uacjcontent.uacj.init.AttributeInit;

import java.util.UUID;

public class SpectralFlightProcessor {
    private static final UUID AMP_UUID = UUID.fromString("f4a5b6c7-d8e9-4a0b-1c2d-3e4f5a6b7c8d");

    private static final int STATE_IDLE = 0;
    private static final int STATE_CHARGING = 1;
    private static final int STATE_ACTIVE = 2;

    public static void serverTick(ServerPlayer player) {
        var data = player.getPersistentData();

        int cooldown = data.getInt("uacj_spectral_cooldown");
        if (cooldown > 0) {
            data.putInt("uacj_spectral_cooldown", cooldown - 1);
        }

        int state = SpectralDataHandler.getState(player);
        if (state == STATE_IDLE) return;

        if (state == STATE_CHARGING) {
            if (player.level().getGameTime() % 40 == 0) {
                if (player.experienceLevel > 0) {
                    player.giveExperienceLevels(-1);
                    data.putInt("uacj_spectral_duration", data.getInt("uacj_spectral_duration") + 2);
                } else {
                    stopFlight(player);
                }
            }
        }

        if (state == STATE_ACTIVE) {
            int ticksLeft = data.getInt("uacj_spectral_ticks");
            if (ticksLeft > 0) {
                player.setInvulnerable(true);
                player.getAbilities().mayfly = true;
                player.getAbilities().flying = true;
                player.getAbilities().setFlyingSpeed(0.12f);

                if (player.zza > 0) {
                    Vec3 motion = player.getDeltaMovement();
                    double yVel = player.getLookAngle().y * 0.6;
                    player.setDeltaMovement(motion.x, yVel, motion.z);
                }

                if (ticksLeft % 10 == 0) player.onUpdateAbilities();

                if (player.level().getGameTime() % 2 == 0) {
                    ((ServerLevel)player.level()).sendParticles(ParticleTypes.SOUL,
                            player.getX(), player.getY() + 0.2, player.getZ(),
                            4, 0.3, 0.1, 0.3, 0.02);
                }

                player.hurtMarked = true;
                data.putInt("uacj_spectral_ticks", ticksLeft - 1);
            } else {
                stopFlight(player);
            }
        }
    }

    public static void toggle(ServerPlayer player) {
        var data = player.getPersistentData();
        int state = SpectralDataHandler.getState(player);
        int cooldown = data.getInt("uacj_spectral_cooldown");

        if (state == STATE_IDLE && cooldown > 0) {
            float seconds = cooldown / 20.0f;
            player.displayClientMessage(Component.literal("§bSpectral Flight cooldown: §f" + String.format("%.1f", seconds) + "s remaining"), true);
            return;
        }

        switch (state) {
            case STATE_IDLE -> {
                SpectralDataHandler.setState(player, STATE_CHARGING);
                data.putInt("uacj_spectral_duration", 6);
            }
            case STATE_CHARGING -> startFlight(player);
            case STATE_ACTIVE -> stopFlight(player);
        }
    }

    private static void startFlight(ServerPlayer player) {
        SpectralDataHandler.setState(player, STATE_ACTIVE);
        player.getPersistentData().putInt("uacj_spectral_ticks", player.getPersistentData().getInt("uacj_spectral_duration") * 20);

        var amp = player.getAttribute(AttributeInit.AMP.get());
        if (amp != null) {
            amp.removeModifier(AMP_UUID);
            amp.addTransientModifier(new AttributeModifier(AMP_UUID, "Spectral Amp", 1.0, AttributeModifier.Operation.ADDITION));
        }

        player.onUpdateAbilities();
    }

    public static void stopFlight(ServerPlayer player) {
        player.setInvulnerable(false);
        SpectralDataHandler.setState(player, STATE_IDLE);

        player.getPersistentData().putInt("uacj_spectral_cooldown", 2400);

        if (!player.isCreative()) {
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
        }
        player.getAbilities().setFlyingSpeed(0.05f);

        var amp = player.getAttribute(AttributeInit.AMP.get());
        if (amp != null) amp.removeModifier(AMP_UUID);

        player.onUpdateAbilities();
    }
}