package org.uacjcontent.uacj.util;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncSkillPacket {
    private final String skillId;
    private final boolean active;

    public SyncSkillPacket(String skillId, boolean active) {
        this.skillId = skillId;
        this.active = active;
    }

    public SyncSkillPacket(FriendlyByteBuf buf) {
        this.skillId = buf.readUtf();
        this.active = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(this.skillId);
        buf.writeBoolean(this.active);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            var player = net.minecraft.client.Minecraft.getInstance().player;
            if (player != null) {
                player.getPersistentData().putBoolean("uacj_skill_" + this.skillId + "_active", this.active);
            }
        });
        ctx.setPacketHandled(true);
        return true;
    }
}
