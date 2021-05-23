package io.github.apace100.origins.power.action.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.power.action.block.BlockAction;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class ExecuteCommandEntityAction implements Consumer<Entity> {

	public static final Codec<ExecuteCommandEntityAction> CODEC = RecordCodecBuilder.create(instance-> instance.group(
			Codec.STRING.fieldOf("command").forGetter(x -> x.command),
			Codec.INT.optionalFieldOf("permission_level", 4).forGetter(x -> x.permissionLevel)
	).apply(instance, ExecuteCommandEntityAction::new));

	private final String command;
	private final int permissionLevel;

	public ExecuteCommandEntityAction(String command, int permissionLevel) {
		this.command = command;
		this.permissionLevel = permissionLevel;
	}

	@Override
	public void accept(Entity entity) {
		MinecraftServer server = entity.world.getServer();
		if(server != null) {
			ServerCommandSource source = new ServerCommandSource(
					CommandOutput.DUMMY,
					entity.getPos(),
					entity.getRotationClient(),
					entity.world instanceof ServerWorld ? (ServerWorld)entity.world : null,
					permissionLevel,
					entity.getName().getString(),
					entity.getDisplayName(),
					entity.world.getServer(),
					entity);
			server.getCommandManager().execute(source, command);
		}
	}
}
