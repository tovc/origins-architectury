package io.github.apace100.origins.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.text.TranslatableText;

import java.util.OptionalInt;
import java.util.concurrent.CompletableFuture;

// Very similar to OperationArgumentType, but modified to make it work with resources.
public class PowerOperation implements ArgumentType<PowerOperation.Operation> {
	public static final SimpleCommandExceptionType INVALID_OPERATION = new SimpleCommandExceptionType(new TranslatableText("arguments.operation.invalid"));
	public static final SimpleCommandExceptionType DIVISION_ZERO_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("arguments.operation.div0"));

	public static PowerOperation operation() {
		return new PowerOperation();
	}

	public PowerOperation.Operation parse(StringReader stringReader) throws CommandSyntaxException {
		if (!stringReader.canRead()) throw INVALID_OPERATION.create();

		int i = stringReader.getCursor();
		while (stringReader.canRead() && stringReader.peek() != ' ') stringReader.skip();

		String stringOperator = stringReader.getString().substring(i, stringReader.getCursor());
		return switch (stringOperator) {
			case "=" -> StandardOperations.ASSIGN;
			case "+=" -> StandardOperations.ADD;
			case "-=" -> StandardOperations.SUBTRACT;
			case "*=" -> StandardOperations.MULTIPLY;
			case "/=" -> StandardOperations.DIVIDE;
			case "%=" -> StandardOperations.MODULUS;
			case "<" -> StandardOperations.MINIMUM;
			case ">" -> StandardOperations.MAXIMUM;
			case "><" -> StandardOperations.SWAP;
			default -> throw INVALID_OPERATION.create();
		};
	}

	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return CommandSource.suggestMatching(new String[]{"=", "+=", "-=", "*=", "/=", "%=", "<", ">", "><"}, builder);
	}

	public enum StandardOperations implements Operation {
		ASSIGN((power, player, score) -> power.assign(player, score.getScore())),
		ADD((power, player, score) -> power.change(player, score.getScore())),
		SUBTRACT((power, player, score) -> power.change(player, -score.getScore())),
		MULTIPLY((power, player, score) -> {
			OptionalInt val = power.getValue(player);
			if (val.isPresent())
				return power.assign(player, val.getAsInt() * score.getScore());
			return val;
		}),
		DIVIDE((power, player, score) -> {
			if (score.getScore() == 0) throw DIVISION_ZERO_EXCEPTION.create();
			OptionalInt val = power.getValue(player);
			if (val.isPresent())
				return power.assign(player, Math.floorDiv(val.getAsInt(), score.getScore()));
			return val;
		}),
		MODULUS((power, player, score) -> {
			if (score.getScore() == 0) throw DIVISION_ZERO_EXCEPTION.create();
			OptionalInt val = power.getValue(player);
			if (val.isPresent())
				return power.assign(player, Math.floorMod(val.getAsInt(), score.getScore()));
			return val;
		}),
		MINIMUM((power, player, score) -> {
			OptionalInt val = power.getValue(player);
			if (val.isPresent())
				return power.assign(player, Math.min(val.getAsInt(), score.getScore()));
			return val;
		}),
		MAXIMUM((power, player, score) -> {
			OptionalInt val = power.getValue(player);
			if (val.isPresent())
				return power.assign(player, Math.max(val.getAsInt(), score.getScore()));
			return val;
		}),
		SWAP((power, player, score) -> {
			OptionalInt val = power.getValue(player);
			if (val.isPresent()) {
				int sc = score.getScore();
				score.setScore(val.getAsInt());
				return power.assign(player, sc);
			}
			return val;
		});

		private final Operation operation;

		StandardOperations(Operation operation) {
			this.operation = operation;
		}

		@Override
		public OptionalInt apply(ConfiguredPower<?, ?> power, PlayerEntity player, ScoreboardPlayerScore score) throws CommandSyntaxException {
			return this.operation.apply(power, player, score);
		}
	}

	public interface Operation {
		OptionalInt apply(ConfiguredPower<?, ?> power, PlayerEntity player, ScoreboardPlayerScore score) throws CommandSyntaxException;
	}
}
