package org.uacjcontent.uacj.init;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class AttributeInit {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, "uacj");

    public static final RegistryObject<Attribute> AMP = ATTRIBUTES.register("enchant_amplification",
            () -> new RangedAttribute("attribute.name.uacj.enchant_amplification", 0.0, 0.0, 1024.0).setSyncable(true));

    public static final RegistryObject<Attribute> NOVICE = ATTRIBUTES.register("novice_enchanter",
            () -> new RangedAttribute("attribute.name.uacj.novice_enchanter", 0.0, 0.0, 1.0).setSyncable(true));

    public static final RegistryObject<Attribute> ADEPT = ATTRIBUTES.register("adept_enchanter",
            () -> new RangedAttribute("attribute.name.uacj.adept_enchanter", 0.0, 0.0, 1.0).setSyncable(true));

    public static final RegistryObject<Attribute> MASTER = ATTRIBUTES.register("master_enchanter",
            () -> new RangedAttribute("attribute.name.uacj.master_enchanter", 0.0, 0.0, 1.0).setSyncable(true));

    public static final RegistryObject<Attribute> EXP_GAIN = ATTRIBUTES.register("experience_gain",
            () -> new RangedAttribute("attribute.name.uacj.experience_gain", 1.0, 0.0, 100.0).setSyncable(true));

    public static final RegistryObject<Attribute> ENCHANT_DISCOUNT = ATTRIBUTES.register("enchantment_discount",
            () -> new RangedAttribute("attribute.name.uacj.enchantment_discount", 0.0, 0.0, 1.0).setSyncable(true));

    public static final RegistryObject<Attribute> ENCHANTED_WEAPON_DAMAGE = ATTRIBUTES.register("enchanted_weapon_damage",
            () -> new RangedAttribute("attribute.name.uacj.enchanted_weapon_damage", 0.0, 0.0, 1024.0).setSyncable(true));

    public static final RegistryObject<Attribute> ENCHANTED_ARMOR_BONUS = ATTRIBUTES.register("enchanted_armor_bonus",
            () -> new RangedAttribute("attribute.name.uacj.enchanted_armor_bonus", 0.0, 0.0, 100.0).setSyncable(true));

    public static final RegistryObject<Attribute> MAGIC_RESISTANCE = ATTRIBUTES.register("magic_resistance",
            () -> new RangedAttribute("attribute.name.uacj.magic_resistance", 0.0, 0.0, 1.0).setSyncable(true));

    @SubscribeEvent
    public static void onAttributeModification(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, AMP.get());
        event.add(EntityType.PLAYER, NOVICE.get());
        event.add(EntityType.PLAYER, ADEPT.get());
        event.add(EntityType.PLAYER, MASTER.get());
        event.add(EntityType.PLAYER, EXP_GAIN.get());
        event.add(EntityType.PLAYER, ENCHANT_DISCOUNT.get());
        event.add(EntityType.PLAYER, ENCHANTED_WEAPON_DAMAGE.get());
        event.add(EntityType.PLAYER, ENCHANTED_ARMOR_BONUS.get());
        event.add(EntityType.PLAYER, MAGIC_RESISTANCE.get());
    }
}