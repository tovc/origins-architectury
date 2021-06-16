package io.github.apace100.origins.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.apace100.origins.api.OriginsAPI;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import net.minecraft.command.CommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class PowerArgument implements ArgumentType<ConfiguredPower<?, ?>> {
	public static final DynamicCommandExceptionType POWER_NOT_FOUND = new DynamicCommandExceptionType((p_208663_0_) -> new TranslatableText("commands.origin.power_not_found", p_208663_0_));

	public static PowerArgument power() {
		return new PowerArgument();
	}

	public ConfiguredPower<?, ?> parse(StringReader reader) throws CommandSyntaxException {
		Identifier id = Identifier.fromCommandInput(reader);
		return OriginsAPI.getPowers().getOrEmpty(id).orElseThrow(() -> POWER_NOT_FOUND.create(id));
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return CommandSource.suggestIdentifiers(OriginsAPI.getPowers().getIds(), builder);
	}
}
