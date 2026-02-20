package org.uacjcontent.uacj.reward;

import java.util.ArrayList;
import java.util.Optional;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.puffish.skillsmod.api.SkillsAPI;
import net.puffish.skillsmod.api.json.JsonElement;
import net.puffish.skillsmod.api.json.JsonObject;
import net.puffish.skillsmod.api.reward.Reward;
import net.puffish.skillsmod.api.reward.RewardConfigContext;
import net.puffish.skillsmod.api.reward.RewardDisposeContext;
import net.puffish.skillsmod.api.reward.RewardUpdateContext;
import net.puffish.skillsmod.api.util.Problem;
import net.puffish.skillsmod.api.util.Result;
import org.uacjcontent.uacj.util.SyncSkillPacket;
import org.uacjcontent.uacj.network.NetworkHandler;

public class SkillReward implements Reward {
    public static final ResourceLocation ID = new ResourceLocation("uacj", "skill");
    private final String skillId;

    private SkillReward(String skillId) {
        this.skillId = skillId;
    }

    public static void register() {
        SkillsAPI.registerReward(ID, SkillReward::create);
    }

    private static Result<SkillReward, Problem> create(RewardConfigContext context) {
        return context.getData().andThen(JsonElement::getAsObject).andThen(SkillReward::parseObject);
    }

    private static Result<SkillReward, Problem> parseObject(JsonObject rootObject) {
        ArrayList<Problem> problems = new ArrayList<>();

        Optional<String> optSkillId = rootObject.get("skill_id")
                .andThen(JsonElement::getAsString)
                .ifFailure(problems::add)
                .getSuccess();

        return problems.isEmpty()
                ? Result.success(new SkillReward(optSkillId.orElseThrow()))
                : Result.failure(Problem.combine(problems));
    }

    @Override
    public void update(RewardUpdateContext context) {
        ServerPlayer player = context.getPlayer();
        player.getPersistentData().putBoolean("uacj_skill_" + this.skillId , true);

        NetworkHandler.CHANNEL.send(net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
                new SyncSkillPacket(this.skillId, true));
    }

    @Override
    public void dispose(RewardDisposeContext context) {
    }
}