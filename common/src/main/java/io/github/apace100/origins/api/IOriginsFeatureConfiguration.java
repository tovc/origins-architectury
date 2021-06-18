package io.github.apace100.origins.api;

import com.google.common.collect.ImmutableList;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.registry.OriginsDynamicRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A global configuration class for all origin features, containing some useful
 * utility methods to help with coding and verification.
 */
public interface IOriginsFeatureConfiguration {
	/**
	 * Checks if this configuration is valid.
	 *
	 * @return The errors that invalidate this configuration, or an empty list in no errors where found.
	 */
	@NotNull
	default List<String> getErrors(@NotNull MinecraftServer server) {
		return ImmutableList.of();
	}

	/**
	 * Returns a list of all warnings that appear during the configuration of this feature.<br/>
	 * If something that really should be here is missing, but it was marked as optional, this
	 * should be written here.
	 */
	@NotNull
	default List<String> getWarnings(@NotNull MinecraftServer server) {
		return ImmutableList.of();
	}

	/**
	 * This is used to check whether this configuration is valid. i.e. if there is a point in executing the power.
	 */
	default boolean isConfigurationValid() { return true; }

	/**
	 * Returns a list of powers that are not registered in the dynamic registry.
	 * @param dynamicRegistryManager The dynamic registry manager, use {@link OriginsDynamicRegistries#get(MinecraftServer)} to access it.
	 * @param identifiers The powers to check the existence of.
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

}