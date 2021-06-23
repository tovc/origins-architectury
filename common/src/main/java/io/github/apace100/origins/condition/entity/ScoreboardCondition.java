package io.github.apace100.origins.condition.entity;

import io.github.apace100.origins.api.power.factory.EntityCondition;
import io.github.apace100.origins.condition.configuration.ScoreboardComparisonConfiguration;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;

public class ScoreboardCondition extends EntityCondition<ScoreboardComparisonConfiguration> {

	public ScoreboardCondition() {
		super(ScoreboardComparisonConfiguration.CODEC);
	}

	@Override
	public boolean check(ScoreboardComparisonConfiguration configuration, LivingEntity entity) {
		if (entity instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) entity;
			Scoreboard scoreboard = player.getScoreboard();
			ScoreboardObjective objective = scoreboard.getObjective(configuration.objective());
			String playerName = player.getName().asString();

			if (scoreboard.playerHasObjective(playerName, objective)) {
				int value = scoreboard.getPlayerScore(playerName, objective).getScore();
				return configuration.comparison().check(value);
			}
		}
		return false;
	}
}
