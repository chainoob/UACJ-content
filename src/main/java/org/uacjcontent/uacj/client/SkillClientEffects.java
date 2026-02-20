package org.uacjcontent.uacj.client;

import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SkillClientEffects {

    @SubscribeEvent
    public static void onComputeFov(ComputeFovModifierEvent event) {
        int timer = event.getPlayer().getPersistentData().getInt("uacj_infusion_timer");
        if (timer > 0 && timer <= 60) {
            float progress = (60 - timer) / 60.0f;
            event.setNewFovModifier(1.0f - (progress * 0.3f));
        }
    }
}
