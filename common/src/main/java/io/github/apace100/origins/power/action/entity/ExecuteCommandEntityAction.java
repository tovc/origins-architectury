package io.github.apace100.origins.power.action.entity;

import io.github.apace100.origins.power.configuration.CommandConfiguration;
import io.github.apace100.origins.api.power.factory.EntityAction;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;

public class ExecuteCommandEntityAction extends EntityAction<CommandConfiguration> {

	public ExecuteCommandEntityAction() {
		super(CommandConfiguration.CODEC);
	}

	@Override
	public void execute(CommandConfiguration configuration, Entity entity) {
		MinecraftServer server = entity.world.getServer();
		if (server != null) {
			ServerCommandSource source = new ServerCommandSource(
					CommandOutput.DUMMY,
					entity.getPos(),
					entity.getRotationClient(),
					entity.world instanceof ServerWorld ? (ServerWorld) entity.world : null,
					configuration.permissionLevel(),
					entity.getName().getString(),
					entity.getDisplayName(),
					entity.world.getServer(),
					entity);
			server.getCommandManager().execute(source, configuration.command());
		}
	}
}
