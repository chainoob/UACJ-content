package org.uacjcontent.uacj.init;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EnchantAmplificationAttribute {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, "uacj");

    public static final RegistryObject<Attribute> AMP = ATTRIBUTES.register("enchant_amplification",
            () -> new RangedAttribute("attribute.name.uacj.enchant_amplification", 0.0, 0.0, 1024.0).setSyncable(true));

    public static void onAttributeModification(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, AMP.get());
    }
}