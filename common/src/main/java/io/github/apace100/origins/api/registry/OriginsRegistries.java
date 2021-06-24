package io.github.apace100.origins.api.registry;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import io.github.apace100.origins.api.OriginsAPI;
import io.github.apace100.origins.api.power.factory.*;
import me.shedaniel.architectury.registry.Registries;
import me.shedaniel.architectury.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;
import net.minecraft.util.registry.RegistryKey;

public class OriginsRegistries {
	public static final Lazy<Registries> REGISTRIES = new Lazy<>(() -> Registries.get(OriginsAPI.MODID));

	public static final RegistryKey<net.minecraft.util.registry.Registry<PowerFactory<?>>> POWER_FACTORY_KEY = RegistryKey.ofRegistry(OriginsAPI.identifier("power_factory"));
	public static final RegistryKey<net.minecraft.util.registry.Registry<EntityCondition<?>>> ENTITY_CONDITION_KEY = RegistryKey.ofRegistry(OriginsAPI.identifier("entity_condition"));
	public static final RegistryKey<net.minecraft.util.registry.Registry<ItemCondition<?>>> ITEM_CONDITION_KEY = RegistryKey.ofRegistry(OriginsAPI.identifier("item_condition"));
	public static final RegistryKey<net.minecraft.util.registry.Registry<BlockCondition<?>>> BLOCK_CONDITION_KEY = RegistryKey.ofRegistry(OriginsAPI.identifier("block_condition"));
	public static final RegistryKey<net.minecraft.util.registry.Registry<DamageCondition<?>>> DAMAGE_CONDITION_KEY = RegistryKey.ofRegistry(OriginsAPI.identifier("damage_condition"));
	public static final RegistryKey<net.minecraft.util.registry.Registry<FluidCondition<?>>> FLUID_CONDITION_KEY = RegistryKey.ofRegistry(OriginsAPI.identifier("fluid_condition"));
	public static final RegistryKey<net.minecraft.util.registry.Registry<BiomeCondition<?>>> BIOME_CONDITION_KEY = RegistryKey.ofRegistry(OriginsAPI.identifier("biome_condition"));
	public static final RegistryKey<net.minecraft.util.registry.Registry<EntityAction<?>>> ENTITY_ACTION_KEY = RegistryKey.ofRegistry(OriginsAPI.identifier("entity_action"));
	public static final RegistryKey<net.minecraft.util.registry.Registry<ItemAction<?>>> ITEM_ACTION_KEY = RegistryKey.ofRegistry(OriginsAPI.identifier("item_action"));
	public static final RegistryKey<net.minecraft.util.registry.Registry<BlockAction<?>>> BLOCK_ACTION_KEY = RegistryKey.ofRegistry(OriginsAPI.identifier("block_action"));

	public static final Registry<PowerFactory<?>> POWER_FACTORY;
	public static final Registry<EntityCondition<?>> ENTITY_CONDITION;
	public static final Registry<ItemCondition<?>> ITEM_CONDITION;
	public static final Registry<BlockCondition<?>> BLOCK_CONDITION;
	public static final Registry<DamageCondition<?>> DAMAGE_CONDITION;
	public static final Registry<FluidCondition<?>> FLUID_CONDITION;
	public static final Registry<BiomeCondition<?>> BIOME_CONDITION;
	public static final Registry<EntityAction<?>> ENTITY_ACTION;
	public static final Registry<ItemAction<?>> ITEM_ACTION;
	public static final Registry<BlockAction<?>> BLOCK_ACTION;

	static {
		Registries registries = REGISTRIES.get();
		//TODO All network calls after login should use integer instead of powers.
		POWER_FACTORY = registries.<PowerFactory<?>>builder(POWER_FACTORY_KEY.getValue()).syncToClients().build();
		ENTITY_CONDITION = registries.<EntityCondition<?>>builder(ENTITY_CONDITION_KEY.getValue()).syncToClients().build();
		ITEM_CONDITION = registries.<ItemCondition<?>>builder(ITEM_CONDITION_KEY.getValue()).syncToClients().build();
		BLOCK_CONDITION = registries.<BlockCondition<?>>builder(BLOCK_CONDITION_KEY.getValue()).syncToClients().build();
		DAMAGE_CONDITION = registries.<DamageCondition<?>>builder(DAMAGE_CONDITION_KEY.getValue()).syncToClients().build();
		FLUID_CONDITION = registries.<FluidCondition<?>>builder(FLUID_CONDITION_KEY.getValue()).syncToClients().build();
		BIOME_CONDITION = registries.<BiomeCondition<?>>builder(BIOME_CONDITION_KEY.getValue()).syncToClients().build();
		ENTITY_ACTION = registries.<EntityAction<?>>builder(ENTITY_ACTION_KEY.getValue()).syncToClients().build();
		ITEM_ACTION = registries.<ItemAction<?>>builder(ITEM_ACTION_KEY.getValue()).syncToClients().build();
		BLOCK_ACTION = registries.<BlockAction<?>>builder(BLOCK_ACTION_KEY.getValue()).syncToClients().build();
	}

	/**
	 * This is basically {@link net.minecraft.util.registry.Registry}, just altered in such a way that it works with
	 * architectury's registries.
	 *
	 * @param registry The registry to create the codec for.
	 *
	 * @return The new codec.
	 */
	public static <T> Codec<T> codec(Registry<T> registry) {
		return new Codec<>() {
			@Override
			public <U> DataResult<Pair<T, U>> decode(DynamicOps<U> dynamicOps, U input) {
				return dynamicOps.compressMaps() ? dynamicOps.getNumberValue(input).flatMap((number) -> {
					T object = registry.byRawId(number.intValue());
					return object == null ? DataResult.error("Unknown registry id: " + number) : DataResult.success(object);
				}).map((objectx) -> Pair.of(objectx, dynamicOps.empty())) : Identifier.CODEC.decode(dynamicOps, input).flatMap((pair) -> {
					T object = registry.get(pair.getFirst());
					return object == null ? DataResult.error("Unknown registry key: " + pair.getFirst()) : DataResult.success(Pair.of(object, pair.getSecond()));
				});
			}

			@Override
			public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
				Identifier identifier = registry.getId(input);
				if (identifier == null) {
					return DataResult.error("Unknown registry element " + input);
				} else {
					return ops.compressMaps() ?
							ops.mergeToPrimitive(prefix, ops.createInt(registry.getRawId(input))) :
							ops.mergeToPrimitive(prefix, ops.createString(identifier.toString()));
				}
			}
		};
	}
}
