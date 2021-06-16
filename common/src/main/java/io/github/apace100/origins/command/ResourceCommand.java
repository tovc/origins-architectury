package io.github.apace100.origins.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.apace100.origins.api.OriginsAPI;
import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.registry.ModComponentsArchitectury;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.ObjectiveArgumentType;
import net.minecraft.command.argument.ScoreHolderArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.OptionalInt;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ResourceCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
            literal("resource").requires(cs -> cs.hasPermissionLevel(2))
                .then(literal("has")
                    .then(argument("target", EntityArgumentType.player())
                        .then(argument("power", PowerArgument.power())
                            .executes((command) -> {return resource(command, Subcommands.HAS);})))
                )
                .then(literal("get")
                    .then(argument("target", EntityArgumentType.player())
                        .then(argument("power", PowerArgument.power())
                            .executes((command) -> {return resource(command, Subcommands.GET);})))
                )
                .then(literal("set")
                    .then(argument("target", EntityArgumentType.player())
                        .then(argument("power", PowerArgument.power())
                            .then(argument("value", IntegerArgumentType.integer())
                                .executes((command) -> {return resource(command, Subcommands.SET);}))))
                )
                .then(literal("change")
                    .then(argument("target", EntityArgumentType.player())
                        .then(argument("power", PowerArgument.power())
                            .then(argument("value", IntegerArgumentType.integer())
                                .executes((command) -> {return resource(command, Subcommands.CHANGE);}))))
                )
                .then(literal("operation")
                    .then(argument("target", EntityArgumentType.player())
                        .then(argument("power", PowerArgument.power())
                            .then(argument("operation", PowerOperation.operation())
                                .then(argument("entity", ScoreHolderArgumentType.scoreHolder())
                                    .then(argument("objective", ObjectiveArgumentType.objective())
                                        .executes((command) -> {return resource(command, Subcommands.OPERATION);}))))))
                )
        );
    }

    public enum Subcommands {
        HAS, GET, SET, CHANGE, OPERATION;
    }

    // This is a cleaner method than sticking it into every subcommand
    private static int resource(CommandContext<ServerCommandSource> command, Subcommands sub) throws CommandSyntaxException {
        //int i = 0;

        ServerPlayerEntity player = EntityArgumentType.getPlayer(command, "target");
        ConfiguredPower<?, ?> powerType = command.getArgument("power", ConfiguredPower.class);
        Identifier identifier = OriginsAPI.getPowers().getId(powerType);

        if (ModComponentsArchitectury.getOriginComponent(player).hasPower(powerType)) {
            OptionalInt result = switch (sub) {
                case HAS -> powerType.asVariableIntPower().map(x -> OptionalInt.of(1)).orElseGet(OptionalInt::empty);
                case GET -> powerType.getValue(player);
                case SET -> powerType.assign(player, IntegerArgumentType.getInteger(command, "value"));
                case CHANGE -> powerType.change(player, IntegerArgumentType.getInteger(command, "value"));
                case OPERATION -> command.getArgument("operation", PowerOperation.Operation.class).apply(powerType, player, command.getSource().getMinecraftServer().getScoreboard().getPlayerScore(ScoreHolderArgumentType.getScoreHolder(command, "entity"), ObjectiveArgumentType.getObjective(command, "objective")));
            };
            switch (sub) {
                case HAS -> {
                    result.ifPresentOrElse(
                            i -> command.getSource().sendFeedback(new TranslatableText("commands.execute.conditional.pass"), true),
                            () -> command.getSource().sendError(new TranslatableText("commands.execute.conditional.fail")));
                    return result.isPresent() ? 1 : 0;
                }
                case GET -> {
                    result.ifPresentOrElse(
                            i -> command.getSource().sendFeedback(new TranslatableText("commands.scoreboard.players.get.success", player.getEntityName(), i, identifier), true),
                            () -> command.getSource().sendError(new TranslatableText("commands.scoreboard.players.get.null", identifier, player.getEntityName())));
                    return result.orElse(0);
                }
                case SET -> {
                    result.ifPresentOrElse(
                            i -> {
                                OriginComponent.sync(player);
                                command.getSource().sendFeedback(new TranslatableText("commands.scoreboard.players.set.success.single", identifier, player.getEntityName(), i), true);
                            }, () -> command.getSource().sendError(new TranslatableText("argument.scoreHolder.empty"))
                    );
                    return result.isPresent() ? 1 : 0;
                }
                case CHANGE -> {
                    result.ifPresentOrElse(
                            i -> {
                                OriginComponent.sync(player);
                                command.getSource().sendFeedback(new TranslatableText("commands.scoreboard.players.add.success.single", i - IntegerArgumentType.getInteger(command, "value"), identifier, player.getEntityName(), i), true);
                            }, () -> command.getSource().sendError(new TranslatableText("argument.scoreHolder.empty"))
                    );
                    return result.isPresent() ? 1 : 0;
                }
                case OPERATION -> {
                    result.ifPresentOrElse(
                            i -> {
                                OriginComponent.sync(player);
                                command.getSource().sendFeedback(new TranslatableText("commands.scoreboard.players.operation.success.single", identifier, player.getEntityName(), i), true);
                            }, () -> command.getSource().sendError(new TranslatableText("argument.scoreHolder.empty"))
                    );
                    return result.isPresent() ? 1 : 0;
                }
            }
        }
        return 0;
    }
}
