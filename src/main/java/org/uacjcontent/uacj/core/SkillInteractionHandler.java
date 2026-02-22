package org.uacjcontent.uacj.core;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "uacj")
public class SkillInteractionHandler {

    static void intercept(PlayerInteractEvent event) {
        var player = event.getEntity();
        if (player.getPersistentData().getBoolean("uacj_skill_infusion") && hasEnchantedBookInInventory(player)) {
            event.setCanceled(true);
            event.setResult(Event.Result.DENY);
        }
    }

    public static boolean hasEnchantedBookInInventory(net.minecraft.world.entity.player.Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (player.getInventory().getItem(i).is(net.minecraft.world.item.Items.ENCHANTED_BOOK)) {
                return true;
            }
        }
        return false;
    }
}