package io.github.apace100.origins.action.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.OptionalInt;

public record CommandConfiguration(String command,
								   int permissionLevel) implements IOriginsFeatureConfiguration {
	public static final MapCodec<CommandConfiguration> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.STRING.fieldOf("command").forGetter(CommandConfiguration::command),
			Codec.INT.optionalFieldOf("permission_level", 4).forGetter(CommandConfiguration::permissionLevel)
	).apply(instance, CommandConfiguration::new));

	public static final Codec<CommandConfiguration> CODEC = MAP_CODEC.codec();

	public OptionalInt execute(Entity entity) {
		MinecraftServer server = entity.world.getServer();
		if (server != null) {
			ServerCommandSource source = new ServerCommandSource(
					CommandOutput.DUMMY,
					entity.getPos(),
					entity.getRotationClient(),
					entity.world instanceof ServerWorld ? (ServerWorld) entity.world : null,
					this.permissionLevel(),
					entity.getName().getString(),
					entity.getDisplayName(),
					entity.world.getServer(),
					entity);
			return OptionalInt.of(server.getCommandManager().execute(source, this.command()));
		}
		return OptionalInt.empty();
	}

	public OptionalInt execute(World world, BlockPos pos) {
		MinecraftServer server = world.getServer();
		if (server != null) {
			String blockName = world.getBlockState(pos).getBlock().getTranslationKey();
			ServerCommandSource source = new ServerCommandSource(
					CommandOutput.DUMMY,
					new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5),
					new Vec2f(0, 0),
					(ServerWorld) world,
					this.permissionLevel(),
					blockName,
					new TranslatableText(blockName),
					server,
					null);
			return OptionalInt.of(server.getCommandManager().execute(source, this.command()));
		}
		return OptionalInt.empty();
	}
}
