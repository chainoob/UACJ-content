package org.uacjcontent.uacj.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = "uacj", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class KeybindHandler {

    public static final KeyMapping SKILL_KEY = new KeyMapping(
            "key.uacj.use_skill",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "category.uacj.skills"
    );

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(SKILL_KEY);
    }
}