package io.github.apace100.origins.command;

import com.mojang.brigadier.CommandDispatcher;
import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayer;
import io.github.apace100.origins.registry.ModComponents;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class OriginCommand {

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(
			literal("origin").requires(cs -> cs.hasPermission(2))
				.then(literal("set")
					.then(argument("targets", EntityArgument.players())
					.then(argument("layer", LayerArgumentType.layer())
					.then(argument("origin", OriginArgumentType.origin())
					.executes((command) -> {
						// Sets the origins of several people in the given layer.
						int i = 0;
						Collection<ServerPlayer> targets = EntityArgument.getPlayers(command, "targets");
						OriginLayer l = LayerArgumentType.getLayer(command, "layer");
						Origin o = OriginArgumentType.getOrigin(command, "origin");
						for(ServerPlayer target : targets) {
							setOrigin(target, l, o);
							i++;
						}
						if (targets.size() == 1) {
							command.getSource().sendSuccess(new TranslatableComponent("commands.origin.set.success.single", targets.iterator().next().getDisplayName(), new TranslatableComponent(l.getTranslationKey()), o.getName()), true);
						} else {
							command.getSource().sendSuccess(new TranslatableComponent("commands.origin.set.success.multiple", targets.size(), new TranslatableComponent(l.getTranslationKey()), o.getName()), true);
						}
						return i;
					}))))
				)
				.then(literal("has")
						.then(argument("targets", EntityArgument.players())
						.then(argument("layer", LayerArgumentType.layer())
						.then(argument("origin", OriginArgumentType.origin())
						.executes((command) -> {
							// Returns the number of people in the target selector with the origin in the given layer.
							// Useful for checking if a player has the given origin in functions.
							int i = 0;
							Collection<ServerPlayer> targets = EntityArgument.getPlayers(command, "targets");
							OriginLayer l = LayerArgumentType.getLayer(command, "layer");
							Origin o = OriginArgumentType.getOrigin(command, "origin");
							for(ServerPlayer target : targets) {
								if (hasOrigin(target, l, o)) {
									i++;
								}
							}
							if (i == 0) {
								command.getSource().sendFailure(new TranslatableComponent("commands.execute.conditional.fail"));
							} else if (targets.size() == 1) {
								command.getSource().sendSuccess(new TranslatableComponent("commands.execute.conditional.pass"), false);
							} else {
								command.getSource().sendSuccess(new TranslatableComponent("commands.execute.conditional.pass_count", i), false);
							}
							return i;
						}))))
				)
			.then(literal("get")
				.then(argument("target", EntityArgument.player())
					.then(argument("layer", LayerArgumentType.layer())
						.executes((command) -> {
							ServerPlayer target = EntityArgument.getPlayer(command, "target");
							OriginLayer layer = LayerArgumentType.getLayer(command, "layer");
							OriginComponent component = ModComponents.ORIGIN.get(target);
							Origin origin = component.getOrigin(layer);
							command.getSource().sendSuccess(new TranslatableComponent("commands.origin.get.result", target.getDisplayName(), new TranslatableComponent(layer.getTranslationKey()), origin.getName(), origin.getIdentifier()), false);
							return 1;
						})
					)
				)
			)
		);
	}

	private static void setOrigin(Player player, OriginLayer layer, Origin origin) {
		OriginComponent component = ModComponents.ORIGIN.get(player);
		component.setOrigin(layer, origin);
		OriginComponent.sync(player);
		boolean hadOriginBefore = component.hadOriginBefore();
		OriginComponent.partialOnChosen(player, hadOriginBefore, origin);
	}

	private static boolean hasOrigin(Player player, OriginLayer layer, Origin origin) {
		OriginComponent component = ModComponents.ORIGIN.get(player);
		return component.hasOrigin(layer) && component.getOrigin(layer).equals(origin);
	}
}
