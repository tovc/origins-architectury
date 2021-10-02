package io.github.apace100.origins.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.edwinmindcraft.origins.api.OriginsAPI;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class OriginArgumentType implements ArgumentType<ResourceLocation> {
	public static final DynamicCommandExceptionType ORIGIN_NOT_FOUND = new DynamicCommandExceptionType((p_208663_0_) -> new TranslatableComponent("commands.origin.origin_not_found", p_208663_0_));

	public static OriginArgumentType origin() {
		return new OriginArgumentType();
	}

	public ResourceLocation parse(StringReader p_parse_1_) throws CommandSyntaxException {
		return ResourceLocation.read(p_parse_1_);
	}

	public static Origin getOrigin(CommandContext<CommandSourceStack> context, String argumentName) throws CommandSyntaxException {
		ResourceLocation id = context.getArgument(argumentName, ResourceLocation.class);
		Origin origin = OriginsAPI.getOriginsRegistry().get(id);
		if (origin == null)
			throw ORIGIN_NOT_FOUND.create(id);
		return origin;
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
			List<ResourceLocation> ids = new LinkedList<>(layer.origins());
			ids.add(Origin.EMPTY.getRegistryName());
			return SharedSuggestionProvider.suggestResource(ids.stream(), builder);
		} else {
			return SharedSuggestionProvider.suggestResource(OriginsAPI.getOriginsRegistry().keySet(), builder);
		}
	}
}