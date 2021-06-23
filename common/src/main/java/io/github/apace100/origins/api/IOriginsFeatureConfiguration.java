package io.github.apace100.origins.api;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.registry.OriginsDynamicRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * A global configuration class for all origin features, containing some useful
 * utility methods to help with coding and verification.
 */
public interface IOriginsFeatureConfiguration {
	static void populate(ImmutableMap.Builder<String, IOriginsFeatureConfiguration> builder, Iterable<?> iterable, String prefix) {
		int i = 0;
		for (Object o : iterable) {
			if (o instanceof IOriginsFeatureConfiguration config)
				builder.put(prefix + i, config);
			else if (o instanceof Map<?, ?> map)
				populate(builder, map, prefix + i + "/");
			else if (o instanceof Iterable<?> iterable2)
				populate(builder, iterable2, prefix + i + "/");
			++i;
		}
	}

	static void populate(ImmutableMap.Builder<String, IOriginsFeatureConfiguration> builder, Map<?, ?> map, String prefix) {
		map.forEach((o, o2) -> {
			String key = "?";
			if (o instanceof String str) key = str;
			else if (o instanceof StringIdentifiable identifiable) key = identifiable.asString();
			if (o2 instanceof IOriginsFeatureConfiguration config)
				builder.put(prefix + key, config);
			else if (o2 instanceof Map<?, ?> map2)
				populate(builder, map, prefix + key + "/");
			else if (o2 instanceof Iterable<?> iterable)
				populate(builder, iterable, prefix + key + "/");
		});
	}

	/**
	 * Checks if this configuration is valid.
	 *
	 * @return The errors that invalidate this configuration, or an empty list in no errors where found.
	 */
	@NotNull
	default List<String> getErrors(@NotNull MinecraftServer server) {
		return this.getChildrenComponent().entrySet().stream().flatMap(entry -> this.copyErrorsFrom(entry.getValue(), server, this.name(), entry.getKey()).stream()).toList();
	}

	/**
	 * Returns a list of all warnings that appear during the configuration of this feature.<br/>
	 * If something that really should be here is missing, but it was marked as optional, this
	 * should be written here.
	 */
	@NotNull
	default List<String> getWarnings(@NotNull MinecraftServer server) {
		return this.getChildrenComponent().entrySet().stream().flatMap(entry -> this.copyWarningsFrom(entry.getValue(), server, this.name(), entry.getKey()).stream()).toList();
	}

	@NotNull
	default String name() {
		String name = this.getClass().getSimpleName();
		if (name.endsWith("Configuration"))
			return name.substring(0, name.length() - "Configuration".length());
		return name;
	}

	@NotNull
	default Map<String, IOriginsFeatureConfiguration> getChildrenComponent() {
		if (this instanceof Record record) {
			ImmutableMap.Builder<String, IOriginsFeatureConfiguration> builder = ImmutableMap.builder();
			for (RecordComponent component : record.getClass().getRecordComponents()) {
				try {
					Object invoke = component.getAccessor().invoke(record);
					if (invoke instanceof IOriginsFeatureConfiguration config)
						builder.put(component.getName(), config);
					else if (invoke instanceof Map<?, ?> map)
						populate(builder, map, component.getName() + "/");
					else if (invoke instanceof Iterable<?> iterable)
						populate(builder, iterable, component.getName() + "/");
				} catch (IllegalAccessException | InvocationTargetException e) {
					Origins.LOGGER.warn("Failed to access record component \"{}\" for \"{}\", auto logging may fail.", component, this.name());
					Origins.LOGGER.debug(e);
				}
			}
			return builder.build();
		}
		return ImmutableMap.of();
	}

	/**
	 * This is used to check whether this configuration is valid. i.e. if there is a point in executing the power.
	 */
	default boolean isConfigurationValid() { return true; }

	/**
	 * Returns a list of powers that are not registered in the dynamic registry.
	 *
	 * @param dynamicRegistryManager The dynamic registry manager, use {@link OriginsDynamicRegistries#get(MinecraftServer)} to access it.
	 * @param identifiers            The powers to check the existence of.
	 *
	 * @return A containing all the missing powers.
	 */
	@NotNull
	default List<Identifier> checkPower(@NotNull IOriginsDynamicRegistryManager dynamicRegistryManager, @NotNull Identifier... identifiers) {
		Registry<ConfiguredPower<?, ?>> powers = dynamicRegistryManager.get(OriginsDynamicRegistries.CONFIGURED_POWER_KEY);
		ImmutableList.Builder<Identifier> builder = ImmutableList.builder();
		for (Identifier identifier : identifiers) {
			if (!powers.containsId(identifier))
				builder.add(identifier);
		}
		return builder.build();
	}

	default UnaryOperator<String> fieldName(String name, String... fields) {
		StringBuilder key = new StringBuilder(name);
		Arrays.stream(fields).flatMap(x -> Arrays.stream(x.split("/"))).filter(x -> !StringUtils.isBlank(x)).forEach(s -> key.append("[").append(s).append("]"));
		String val = key.append("/%s").toString();
		return val::formatted;
	}

	default List<String> copyErrorsFrom(@Nullable IOriginsFeatureConfiguration config, MinecraftServer server, String name, String... fields) {
		if (config == null) return ImmutableList.of();
		return config.getErrors(server).stream().map(fieldName(name, fields)).toList();
	}

	default List<String> copyWarningsFrom(@Nullable IOriginsFeatureConfiguration config, MinecraftServer server, String name, String... fields) {
		if (config == null) return ImmutableList.of();
		return config.getWarnings(server).stream().map(fieldName(name, fields)).toList();
	}
}