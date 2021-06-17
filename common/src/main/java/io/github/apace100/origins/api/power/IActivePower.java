package io.github.apace100.origins.api.power;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import net.minecraft.entity.player.PlayerEntity;

import java.util.function.Function;

public interface IActivePower<T extends IOriginsFeatureConfiguration> {

	void activate(ConfiguredPower<T, ?> configuration, PlayerEntity player);

	IActivePower.Key getKey(ConfiguredPower<T, ?> configuration, PlayerEntity player);

	record Key(String key, boolean continuous) {
		public static final Key PRIMARY = new Key("key.origins.primary_active", false);
		public static final Key SECONDARY = new Key("key.origins.secondary_active", false);

		public static final Codec<Key> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.STRING.fieldOf("key").forGetter(Key::key),
				Codec.BOOL.optionalFieldOf("continuous", false).forGetter(Key::continuous)
		).apply(instance, Key::new));

		public static final Codec<Key> BACKWARD_COMPATIBLE_CODEC = Codec.either(CODEC, Codec.STRING).xmap(x -> x.map(Function.identity(), string -> new Key(string.equals("secondary") ? SECONDARY.key() : PRIMARY.key(), false)), Either::left);
	}
}
