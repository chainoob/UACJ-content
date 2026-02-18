package org.uacjcontent.uacj.core;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.puffish.skillsmod.api.reward.Reward;
import net.puffish.skillsmod.api.reward.RewardDisposeContext;
import net.puffish.skillsmod.api.reward.RewardUpdateContext;
import net.puffish.skillsmod.api.reward.RewardConfigContext;
import net.puffish.skillsmod.api.json.JsonElement;
import net.puffish.skillsmod.api.util.Result;
import net.puffish.skillsmod.api.util.Problem;
import org.uacjcontent.uacj.init.AttributeInit;

import java.util.UUID;

public class EnchantmentAmplificationReward implements Reward {
    public static final ResourceLocation ID = new ResourceLocation("uacj", "enchantment_amplification");

    private static final UUID MODIFIER_UUID = UUID.fromString("f4b1e2a0-7d1c-4b3a-9e5f-1234567890ab");
    private final float amount;
    private UUID playerUUID;

    public EnchantmentAmplificationReward(float amount) {
        this.amount = amount;
    }

    @Override
    public void update(RewardUpdateContext context) {
        ServerPlayer player = context.getPlayer();
        this.playerUUID = player.getUUID();

        AttributeInstance instance = player.getAttribute(AttributeInit.AMP.get());
        if (instance != null && instance.getModifier(MODIFIER_UUID) == null) {
            instance.addTransientModifier(new AttributeModifier(
                    MODIFIER_UUID,
                    "Enchantment Amplification Bonus",
                    this.amount,
                    AttributeModifier.Operation.ADDITION
            ));
        }
    }

    @Override
    public void dispose(RewardDisposeContext context) {
        if (this.playerUUID != null) {
            ServerPlayer player = context.getServer().getPlayerList().getPlayer(this.playerUUID);
            if (player != null) {
                AttributeInstance instance = player.getAttribute(AttributeInit.AMP.get());
                if (instance != null) {
                    instance.removeModifier(MODIFIER_UUID);
                }
            }
        }
    }

    // 2. Define the parse method here for SkillsAPI
    public static Result<EnchantmentAmplificationReward, Problem> parse(RewardConfigContext context) {
        return context.getData()
                .andThen(JsonElement::getAsObject)
                .andThen(obj -> obj.get("amount"))
                .andThen(JsonElement::getAsFloat)
                .mapSuccess(EnchantmentAmplificationReward::new);
    }
}