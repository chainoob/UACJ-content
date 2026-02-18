package org.uacjcontent.uacj.util;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import java.lang.reflect.Field;

public class ReflectionHelper {
    private static final Field DUR = ObfuscationReflectionHelper.findField(MobEffectInstance.class, "f_19503_");

    public static void setDuration(MobEffectInstance effect, int ticks) throws IllegalAccessException {
        DUR.set(effect, ticks);
    }
}