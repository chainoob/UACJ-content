package org.uacjcontent.uacj.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.uacjcontent.uacj.util.SyncSkillPacket;

import java.util.Optional;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("uacj", "main_channel"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++,
                SyncSkillPacket.class,
                SyncSkillPacket::toBytes,
                SyncSkillPacket::new,
                SyncSkillPacket::handle);
        CHANNEL.registerMessage(id++,
                ExecuteInfusionPacket.class,
                ExecuteInfusionPacket::toBytes,
                ExecuteInfusionPacket::new,
                ExecuteInfusionPacket::handle);
        CHANNEL.registerMessage(id++,
                S2CSyncGlowPacket.class,
                S2CSyncGlowPacket::toBytes,
                S2CSyncGlowPacket::fromBytes,
                S2CSyncGlowPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(id++,
                C2SSpectralTogglePacket.class,
                C2SSpectralTogglePacket::toBytes,
                C2SSpectralTogglePacket::fromBytes,
                C2SSpectralTogglePacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );
    }
}