package io.github.apace100.origins.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayer;
import io.github.apace100.origins.origin.OriginRegistry;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

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
      try {
         return OriginRegistry.get(id);
      } catch(IllegalArgumentException e) {
         throw ORIGIN_NOT_FOUND.create(id);
      }
   }

   @Override
   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      OriginLayer layer = null;
      try {
         layer = context.getArgument("layer", OriginLayer.class);
      } catch(Exception e) {
         // no-op :)
      }
      if(layer != null) {
         List<ResourceLocation> ids = new LinkedList<>(layer.getOrigins());
         ids.add(Origin.EMPTY.getIdentifier());
         return SharedSuggestionProvider.suggestResource(ids.stream(), builder);
      } else {
         return SharedSuggestionProvider.suggestResource(OriginRegistry.identifiers(), builder);
      }
   }
}