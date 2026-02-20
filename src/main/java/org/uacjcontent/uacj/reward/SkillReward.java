package org.uacjcontent.uacj.skills;

import java.util.ArrayList;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.puffish.skillsmod.api.SkillsAPI;
import net.puffish.skillsmod.api.json.JsonElement;
import net.puffish.skillsmod.api.json.JsonObject;
import net.puffish.skillsmod.api.reward.Reward;
import net.puffish.skillsmod.api.reward.RewardConfigContext;
import net.puffish.skillsmod.api.reward.RewardDisposeContext;
import net.puffish.skillsmod.api.reward.RewardUpdateContext;
import net.puffish.skillsmod.api.util.Problem;
import net.puffish.skillsmod.api.util.Result;

public class PassiveSkillReward implements Reward {
    public static final ResourceLocation ID = new ResourceLocation("uacj", "passive_skills");
    private final float multiplier;

    private PassiveSkillReward(float multiplier) {
        this.multiplier = multiplier;
    }

    public static void register() {
        SkillsAPI.registerReward(ID, PassiveSkillReward::create);
    }

    private static Result<PassiveSkillReward, Problem> create(RewardConfigContext context) {
        return context.getData().andThen(JsonElement::getAsObject).andThen(PassiveSkillReward::parseObject);
    }

    private static Result<PassiveSkillReward, Problem> parseObject(JsonObject rootObject) {
        ArrayList<Problem> problems = new ArrayList<>();

        Optional<Float> optMultiplier = rootObject.get("multiplier")
                .andThen(JsonElement::getAsFloat)
                .ifFailure(problems::add)
                .getSuccess();

        return problems.isEmpty()
                ? Result.success(new PassiveSkillReward(optMultiplier.orElseThrow()))
                : Result.failure(Problem.combine(problems));
    }

    @Override
    public void update(RewardUpdateContext context) {
        context.getPlayer().getPersistentData().putFloat("uacj_passive_skill_multi", this.multiplier);
    }

    @Override
    public void dispose(RewardDisposeContext context) {
    }
}