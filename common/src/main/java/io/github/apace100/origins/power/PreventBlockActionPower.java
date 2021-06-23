package io.github.apace100.origins.power;

import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.configuration.FieldConfiguration;
import io.github.apace100.origins.api.power.configuration.ConfiguredBlockCondition;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.registry.ModPowers;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public class PreventBlockActionPower extends PowerFactory<FieldConfiguration<Optional<ConfiguredBlockCondition<?, ?>>>> {
	public static boolean isSelectionPrevented(Entity entity, BlockPos pos) {
		CachedBlockPosition position = new CachedBlockPosition(entity.world, pos, true);
		return OriginComponent.getPowers(entity, ModPowers.PREVENT_BLOCK_SELECTION.get()).stream().anyMatch(x -> x.getFactory().doesPrevent(x, position));
	}

	public static boolean isUsagePrevented(Entity entity, BlockPos pos) {
		CachedBlockPosition position = new CachedBlockPosition(entity.world, pos, true);
		return OriginComponent.getPowers(entity, ModPowers.PREVENT_BLOCK_USAGE.get()).stream().anyMatch(x -> x.getFactory().doesPrevent(x, position));
	}

	public PreventBlockActionPower() {
		super(FieldConfiguration.optionalCodec(ConfiguredBlockCondition.CODEC, "block_condition"));
	}

	public boolean doesPrevent(ConfiguredPower<FieldConfiguration<Optional<ConfiguredBlockCondition<?, ?>>>, ?> configuration, CachedBlockPosition position) {
		return configuration.getConfiguration().value().map(x -> x.check(position)).orElse(true);
	}
}
