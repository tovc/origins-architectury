package io.github.apace100.origins.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.apace100.origins.api.OriginsAPI;
import io.github.apace100.origins.api.origin.OriginLayer;
import net.minecraft.command.CommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class LayerArgument implements ArgumentType<OriginLayer> {
	public static final DynamicCommandExceptionType LAYER_NOT_FOUND = new DynamicCommandExceptionType((p_208663_0_) -> new TranslatableText("commands.origin.layer_not_found", p_208663_0_));

	public static LayerArgument layer() {
		return new LayerArgument();
	}

	public OriginLayer parse(StringReader p_parse_1_) throws CommandSyntaxException {
		Identifier id = Identifier.fromCommandInput(p_parse_1_);
		return OriginsAPI.getLayers().getOrEmpty(id).orElseThrow(() -> LAYER_NOT_FOUND.create(id));
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return CommandSource.suggestIdentifiers(OriginsAPI.getLayers().getIds(), builder);
	}
}