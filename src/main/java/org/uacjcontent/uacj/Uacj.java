package org.uacjcontent.uacj;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.uacjcontent.uacj.network.NetworkHandler;
import org.uacjcontent.uacj.core.*;
import org.uacjcontent.uacj.init.AttributeInit;
import org.uacjcontent.uacj.reward.SkillReward;

@Mod("uacj")
public class Uacj {
    public Uacj(FMLJavaModLoadingContext context) {
        IEventBus modBus = context.getModEventBus();

        AttributeInit.ATTRIBUTES.register(modBus);
        modBus.addListener(AttributeInit::onAttributeModification);

        SkillReward.register();

        modBus.addListener(this::setup);
        context.registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, UacjConfig.SPEC);
    }

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(NetworkHandler::register);
    }
}