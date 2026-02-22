package org.uacjcontent.uacj.core;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.player.Player;

public class SpectralDataHandler {
    public static final EntityDataAccessor<Integer> SPECTRAL_STATE = SynchedEntityData.defineId(Player.class, EntityDataSerializers.INT);

    public static void setState(Player player, int state) {
        player.getEntityData().set(SPECTRAL_STATE, state);
    }

    public static int getState(Player player) {
        return player.getEntityData().get(SPECTRAL_STATE);
    }

    public static boolean isSpectral(Player player) {
        return getState(player) == 2;
    }
}