package io.github.apace100.origins.registry.action;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.action.block.*;
import io.github.apace100.origins.api.power.configuration.ConfiguredBlockAction;
import io.github.apace100.origins.api.power.configuration.ConfiguredBlockCondition;
import io.github.apace100.origins.api.power.factory.BlockAction;
import io.github.apace100.origins.api.registry.OriginsRegistries;
import io.github.apace100.origins.factory.MetaFactories;
import me.shedaniel.architectury.registry.RegistrySupplier;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Triple;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

public class ModBlockActions {
	public static final BiConsumer<ConfiguredBlockAction<?, ?>, Triple<World, BlockPos, Direction>> EXECUTOR = (action, o) -> action.execute(o.getLeft(), o.getMiddle(), o.getRight());
	public static final BiPredicate<ConfiguredBlockCondition<?, ?>, Triple<World, BlockPos, Direction>> PREDICATE = (condition, triple) -> condition.check(new CachedBlockPosition(triple.getLeft(), triple.getMiddle(), true));

	public static final RegistrySupplier<OffsetAction> OFFSET = register("offset", OffsetAction::new);
	public static final RegistrySupplier<SetBlockAction> SET_BLOCK = register("set_block", SetBlockAction::new);
	public static final RegistrySupplier<AddBlockAction> ADD_BLOCK = register("add_block", AddBlockAction::new);
	public static final RegistrySupplier<ExecuteCommandBlockAction> EXECUTE_COMMAND = register("execute_command", ExecuteCommandBlockAction::new);

	public static void register() {
		MetaFactories.defineMetaActions(OriginsRegistries.BLOCK_ACTION, DelegatedBlockAction::new, ConfiguredBlockAction.CODEC, ConfiguredBlockCondition.CODEC, EXECUTOR, PREDICATE);
	}

	@SuppressWarnings("unchecked")
	private static <T extends BlockAction<?>> RegistrySupplier<T> register(String name, Supplier<T> factory) {
		return (RegistrySupplier<T>) OriginsRegistries.BLOCK_ACTION.registerSupplied(Origins.identifier(name), factory::get);
	}
}
