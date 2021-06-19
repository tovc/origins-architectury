package io.github.apace100.origins.action.block;

import io.github.apace100.origins.action.configuration.CommandConfiguration;
import io.github.apace100.origins.api.power.factory.BlockAction;
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

public class ExecuteCommandBlockAction extends BlockAction<CommandConfiguration> {
	public ExecuteCommandBlockAction() {
		super(CommandConfiguration.CODEC);
	}

	@Override
	public void execute(CommandConfiguration configuration, World world, BlockPos pos, Direction direction) {
		MinecraftServer server = world.getServer();
		if (server != null) {
			String blockName = world.getBlockState(pos).getBlock().getTranslationKey();
			ServerCommandSource source = new ServerCommandSource(
					CommandOutput.DUMMY,
					new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5),
					new Vec2f(0, 0),
					(ServerWorld) world,
					configuration.permissionLevel(),
					blockName,
					new TranslatableText(blockName),
					server,
					null);
			server.getCommandManager().execute(source, configuration.command());
		}
	}
}
