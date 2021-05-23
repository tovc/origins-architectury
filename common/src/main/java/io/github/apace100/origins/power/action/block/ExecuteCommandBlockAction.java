package io.github.apace100.origins.power.action.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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

public class ExecuteCommandBlockAction implements BlockAction{

	public static final Codec<ExecuteCommandBlockAction> CODEC = RecordCodecBuilder.create(instance-> instance.group(
			Codec.STRING.fieldOf("command").forGetter(x -> x.command),
			Codec.INT.optionalFieldOf("permission_level", 4).forGetter(x -> x.permissionLevel)
	).apply(instance, ExecuteCommandBlockAction::new));

	private final String command;
	private final int permissionLevel;

	public ExecuteCommandBlockAction(String command, int permissionLevel) {
		this.command = command;
		this.permissionLevel = permissionLevel;
	}

	@Override
	public void accept(World world, BlockPos pos, Direction direction) {
		MinecraftServer server = world.getServer();
		if(server != null) {
			String blockName = world.getBlockState(pos).getBlock().getTranslationKey();
			ServerCommandSource source = new ServerCommandSource(
					CommandOutput.DUMMY,
					new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5),
					new Vec2f(0, 0),
					(ServerWorld)world,
					this.permissionLevel,
					blockName,
					new TranslatableText(blockName),
					server,
					null);
			server.getCommandManager().execute(source, this.command);
		}
	}
}
