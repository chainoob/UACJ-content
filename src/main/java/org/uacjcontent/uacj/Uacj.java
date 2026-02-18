package org.uacjcontent.uacj;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.puffish.skillsmod.api.SkillsAPI;
import org.uacjcontent.uacj.core.*;

@Mod("uacj")
public class Uacj {
    public Uacj(FMLJavaModLoadingContext context) {
        IEventBus modBus = context.getModEventBus();

        EnchantAmplificationAttribute.ATTRIBUTES.register(modBus);
        modBus.addListener(EnchantAmplificationAttribute::onAttributeModification);

        MinecraftForge.EVENT_BUS.register(DefectProcessor.class);

        modBus.addListener(this::setup);
        context.registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, UacjConfig.SPEC);
    }

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> SkillsAPI.registerReward(EnchantmentAmplificationReward.ID, EnchantmentAmplificationReward::parse));
    }
}