package io.github.apace100.origins.registry.condition;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.api.power.configuration.ConfiguredBlockCondition;
import io.github.apace100.origins.api.power.factory.BlockCondition;
import io.github.apace100.origins.api.registry.OriginsRegistries;
import io.github.apace100.origins.condition.block.*;
import io.github.apace100.origins.factory.MetaFactories;
import me.shedaniel.architectury.registry.RegistrySupplier;
import net.minecraft.block.pattern.CachedBlockPosition;

import java.util.function.BiPredicate;
import java.util.function.Supplier;

public class ModBlockConditions {
	public static final BiPredicate<ConfiguredBlockCondition<?, ?>, CachedBlockPosition> PREDICATE = (config, position) -> config.check(position);

	public static final RegistrySupplier<SimpleBlockCondition> MOVEMENT_BLOCKING = register("movement_blocking", () -> new SimpleBlockCondition(SimpleBlockCondition.MOVEMENT_BLOCKING));
	public static final RegistrySupplier<SimpleBlockCondition> REPLACEABLE_LEGACY = register("replacable", () -> new SimpleBlockCondition(SimpleBlockCondition.REPLACEABLE)); //This one has a typo.
	public static final RegistrySupplier<SimpleBlockCondition> REPLACEABLE = register("replaceable", () -> new SimpleBlockCondition(SimpleBlockCondition.REPLACEABLE)); //This one doesn't have a typo.
	public static final RegistrySupplier<SimpleBlockCondition> LIGHT_BLOCKING = register("light_blocking", () -> new SimpleBlockCondition(SimpleBlockCondition.LIGHT_BLOCKING));
	public static final RegistrySupplier<SimpleBlockCondition> WATER_LOGGABLE = register("water_loggable", () -> new SimpleBlockCondition(SimpleBlockCondition.WATER_LOGGABLE));
	public static final RegistrySupplier<SimpleBlockCondition> EXPOSED_TO_SKY = register("exposed_to_sky", () -> new SimpleBlockCondition(SimpleBlockCondition.EXPOSED_TO_SKY));
	public static final RegistrySupplier<InTagBlockCondition> IN_TAG = register("in_tag", InTagBlockCondition::new);
	public static final RegistrySupplier<FluidBlockCondition> FLUID = register("fluid", FluidBlockCondition::new);
	public static final RegistrySupplier<OffsetCondition> OFFSET = register("offset", OffsetCondition::new);
	public static final RegistrySupplier<AttachableCondition> ATTACHABLE = register("attachable", AttachableCondition::new);
	public static final RegistrySupplier<BlockTypeCondition> BLOCK = register("block", BlockTypeCondition::new);
	public static final RegistrySupplier<AdjacentCondition> ADJACENT = register("adjacent", AdjacentCondition::new);
	public static final RegistrySupplier<LightLevelCondition> LIGHT_LEVEL = register("light_level", LightLevelCondition::new);
	public static final RegistrySupplier<BlockStateCondition> BLOCK_STATE = register("block_state", BlockStateCondition::new);
	public static final RegistrySupplier<HeightCondition> HEIGHT = register("height", HeightCondition::new);

	public static void register() {
		MetaFactories.defineMetaConditions(OriginsRegistries.BLOCK_CONDITION, DelegatedBlockCondition::new, ConfiguredBlockCondition.CODEC, PREDICATE);
	}

	@SuppressWarnings("unchecked")
	private static <T extends BlockCondition<?>> RegistrySupplier<T> register(String name, Supplier<T> factory) {
		return (RegistrySupplier<T>) OriginsRegistries.BLOCK_CONDITION.registerSupplied(Origins.identifier(name), factory::get);
	}
}
