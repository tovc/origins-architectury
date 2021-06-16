package io.github.apace100.origins.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.apace100.origins.api.OriginsAPI;
import io.github.apace100.origins.api.origin.Origin;
import io.github.apace100.origins.api.origin.OriginLayer;
import io.github.apace100.origins.registry.ModOrigins;
import net.minecraft.command.CommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class OriginArgument implements ArgumentType<Origin> {
	public static final DynamicCommandExceptionType ORIGIN_NOT_FOUND = new DynamicCommandExceptionType((p_208663_0_) -> new TranslatableText("commands.origin.origin_not_found", p_208663_0_));

	public static OriginArgument origin() {
		return new OriginArgument();
	}

	public Origin parse(StringReader p_parse_1_) throws CommandSyntaxException {
		Identifier id = Identifier.fromCommandInput(p_parse_1_);
		return OriginsAPI.getOrigins().getOrEmpty(id).orElseThrow(() -> ORIGIN_NOT_FOUND.create(id));
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		OriginLayer layer = null;
		try {
			layer = context.getArgument("layer", OriginLayer.class);
		} catch (Exception e) {
			// no-op :)
		}
		if (layer != null) {
			return CommandSource.suggestIdentifiers(Stream.concat(layer.origins(), Stream.of(ModOrigins.EMPTY_KEY.getValue())), builder);
		} else {
			return CommandSource.suggestIdentifiers(OriginsAPI.getOrigins().getIds(), builder);
		}
	}
}