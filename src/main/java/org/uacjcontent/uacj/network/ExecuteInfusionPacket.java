package org.uacjcontent.uacj.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.uacjcontent.uacj.core.EnchantInfusionHandler;

import java.util.function.Supplier;

public class ExecuteInfusionPacket {

    public ExecuteInfusionPacket() {}
    public ExecuteInfusionPacket(FriendlyByteBuf buf) {}
    public void toBytes(FriendlyByteBuf buf) {}

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) return;
            EnchantInfusionHandler.processInfusion(player);
        });
        ctx.setPacketHandled(true);
    }
}