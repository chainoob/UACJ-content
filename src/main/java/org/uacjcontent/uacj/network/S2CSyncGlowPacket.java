package org.uacjcontent.uacj.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public record S2CSyncGlowPacket(boolean active) {
    public void toBytes(FriendlyByteBuf buf) { buf.writeBoolean(active); }
    public static S2CSyncGlowPacket fromBytes(FriendlyByteBuf buf) { return new S2CSyncGlowPacket(buf.readBoolean()); }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            var player = Minecraft.getInstance().player;
            if (player != null) player.getPersistentData().putBoolean("uacj_harvest_glow", active);
        }));
        supplier.get().setPacketHandled(true);
    }
}