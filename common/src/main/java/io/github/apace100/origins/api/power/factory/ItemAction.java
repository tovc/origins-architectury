package io.github.apace100.origins.api.power.factory;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.IFactory;
import io.github.apace100.origins.api.power.configuration.ConfiguredItemAction;
import io.github.apace100.origins.api.registry.OriginsRegistries;
import me.shedaniel.architectury.core.RegistryEntry;
import net.minecraft.item.ItemStack;

public abstract class ItemAction<T extends IOriginsFeatureConfiguration> extends RegistryEntry<ItemAction<?>> implements IFactory<T, ConfiguredItemAction<T, ?>, ItemAction<T>> {
	public static final Codec<ItemAction<?>> CODEC = OriginsRegistries.codec(OriginsRegistries.ITEM_ACTION);
	private final Codec<T> codec;

	protected ItemAction(Codec<T> codec) {
		this.codec = codec;
	}

	@Override
	public Codec<T> getCodec() {
		return codec;
	}

	@Override
	public final ConfiguredItemAction<T, ?> configure(T input) {
		return new ConfiguredItemAction<>(this, input);
	}

	public abstract void execute(T configuration, ItemStack stack);
}
