package io.github.apace100.origins.api.power.factory;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.IFactory;
import io.github.apace100.origins.api.power.configuration.ConfiguredEntityAction;
import io.github.apace100.origins.api.registry.OriginsRegistries;
import me.shedaniel.architectury.core.RegistryEntry;
import net.minecraft.entity.Entity;

public abstract class EntityAction<T extends IOriginsFeatureConfiguration> extends RegistryEntry<EntityAction<?>> implements IFactory<T, ConfiguredEntityAction<T, ?>, EntityAction<T>> {
	public static final Codec<EntityAction<?>> CODEC = OriginsRegistries.codec(OriginsRegistries.ENTITY_ACTION);

	private final Codec<T> codec;

	protected EntityAction(Codec<T> codec) {
		this.codec = codec;
	}

	@Override
	public Codec<T> getCodec() {
		return codec;
	}

	@Override
	public final ConfiguredEntityAction<T, ?> configure(T input) {
		return new ConfiguredEntityAction<>(this, input);
	}

	public abstract void execute(T configuration, Entity entity);
}
