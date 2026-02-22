package org.uacjcontent.uacj.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.uacjcontent.uacj.core.SpectralFlightProcessor;
import java.util.function.Supplier;

public class C2SSpectralTogglePacket {
    public C2SSpectralTogglePacket() {}
    public C2SSpectralTogglePacket(FriendlyByteBuf buf) {}
    public void toBytes(FriendlyByteBuf buf) {}

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            ServerPlayer player = supplier.get().getSender();
            if (player != null) SpectralFlightProcessor.toggle(player);
        });
        supplier.get().setPacketHandled(true);
    }
}