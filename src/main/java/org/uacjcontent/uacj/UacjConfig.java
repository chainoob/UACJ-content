package org.uacjcontent.uacj;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = "uacj", bus = Mod.EventBusSubscriber.Bus.MOD)
public class UacjConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.DoubleValue GLOBAL_CAP = BUILDER
            .comment("Maximum total multiplier allowed for scaling (e.g., 5.0 = 500% bonus).")
            .defineInRange("globalMultiplierCap", 5.0, 0.0, 100.0);

    public static final ForgeConfigSpec.BooleanValue ENABLE_THORNS = BUILDER
            .comment("Amplify Thorns and reactionary damage types.")
            .define("enableThorns", true);

    public static final ForgeConfigSpec.BooleanValue ENABLE_DEFECTS = BUILDER
            .comment("Amplify status effects (Fire, Frozen, and Mob Effects).")
            .define("enableDefects", true);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static float globalCap;
    public static boolean enableThorns;
    public static boolean enableDefects;

    @SubscribeEvent
    static void onConfigSync(final ModConfigEvent event) {
        if (event.getConfig().getSpec() == SPEC) {
            globalCap = GLOBAL_CAP.get().floatValue();
            enableThorns = ENABLE_THORNS.get();
            enableDefects = ENABLE_DEFECTS.get();
        }
    }
}