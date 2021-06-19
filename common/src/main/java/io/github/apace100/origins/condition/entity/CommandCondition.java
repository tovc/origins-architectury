package io.github.apace100.origins.condition.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.util.Comparison;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;

import java.util.function.Predicate;

public class CommandCondition implements Predicate<LivingEntity> {
	public static final Codec<CommandCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("command").forGetter(x -> x.command),
			Codec.INT.optionalFieldOf("permission_level", 4).forGetter(x -> x.permissionLevel),
			OriginsCodecs.COMPARISON.fieldOf("comparison").forGetter(x -> x.comparison),
			Codec.INT.fieldOf("compare_to").forGetter(x -> x.compareTo)
	).apply(instance, CommandCondition::new));

	private final String command;
	private final int permissionLevel;
	private final Comparison comparison;
	private final int compareTo;

	public CommandCondition(String command, int permissionLevel, Comparison comparison, int compareTo) {
		this.command = command;
		this.permissionLevel = permissionLevel;
		this.comparison = comparison;
		this.compareTo = compareTo;
	}

	@Override
	public boolean test(LivingEntity entity) {
		MinecraftServer server = entity.world.getServer();
		if (server != null) {
			ServerCommandSource source = new ServerCommandSource(
					CommandOutput.DUMMY,
					entity.getPos(),
					entity.getRotationClient(),
					entity.world instanceof ServerWorld ? (ServerWorld) entity.world : null,
					this.permissionLevel,
					entity.getName().getString(),
					entity.getDisplayName(),
					server,
					entity);
			int output = server.getCommandManager().execute(source, this.command);
			return this.comparison.compare(output, this.compareTo);
		}
		return false;
	}
}
