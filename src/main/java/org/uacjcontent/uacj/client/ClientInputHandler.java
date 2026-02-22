package org.uacjcontent.uacj.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.uacjcontent.uacj.network.C2SSpectralTogglePacket;
import org.uacjcontent.uacj.network.NetworkHandler;

@Mod.EventBusSubscriber(modid = "uacj", value = Dist.CLIENT)
public class ClientInputHandler {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (KeybindHandler.SKILL_KEY.consumeClick()) {
            NetworkHandler.CHANNEL.sendToServer(new C2SSpectralTogglePacket());
        }
    }
}
