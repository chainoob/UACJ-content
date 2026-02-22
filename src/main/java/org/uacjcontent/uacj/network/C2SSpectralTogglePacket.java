package org.uacjcontent.uacj.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import org.uacjcontent.uacj.core.SpectralFlightProcessor;
import java.util.function.Supplier;

public class C2SSpectralTogglePacket {

    public C2SSpectralTogglePacket() {}
    public void toBytes(FriendlyByteBuf buf) {}

    public static C2SSpectralTogglePacket fromBytes(FriendlyByteBuf buf) {
        return new C2SSpectralTogglePacket();
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) return;

            CompoundTag persistedData = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
            boolean hasSkill = persistedData.getBoolean("uacj_skill_spectral_flight");

            if (hasSkill) {
                SpectralFlightProcessor.toggle(player);
            }
        });
        ctx.setPacketHandled(true);
    }
}