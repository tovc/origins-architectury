package io.github.apace100.origins.power.condition.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.util.Comparison;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;

import java.util.function.Predicate;

public class ScoreboardCondition implements Predicate<LivingEntity> {
	public static Codec<ScoreboardCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.COMPARISON.fieldOf("comparison").forGetter(x -> x.comparison),
			Codec.INT.fieldOf("compare_to").forGetter(x -> x.compareTo),
			Codec.STRING.fieldOf("objective").forGetter(x -> x.objective)
	).apply(instance, ScoreboardCondition::new));

	private final Comparison comparison;
	private final int compareTo;
	private final String objective;

	public ScoreboardCondition(Comparison comparison, int compareTo, String objective) {
		this.comparison = comparison;
		this.compareTo = compareTo;
		this.objective = objective;
	}

	@Override
	public boolean test(LivingEntity entity) {
		if (entity instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) entity;
			Scoreboard scoreboard = player.getScoreboard();
			ScoreboardObjective objective = scoreboard.getObjective(this.objective);
			String playerName = player.getName().asString();

			if (scoreboard.playerHasObjective(playerName, objective)) {
				int value = scoreboard.getPlayerScore(playerName, objective).getScore();
				return this.comparison.compare(value, this.compareTo);
			}
		}
		return false;
	}
}
