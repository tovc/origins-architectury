package io.github.apace100.origins.api.configuration;

import com.google.common.collect.ImmutableMap;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Used to avoid boilerplate code by implementing {@link IOriginsFeatureConfiguration#getWarnings(MinecraftServer)} and
 * {@link IOriginsFeatureConfiguration#getErrors(MinecraftServer)} for children.
 */
public interface IStreamConfiguration<T> extends IOriginsFeatureConfiguration {
	List<T> entries();

	@Override
	default @NotNull Map<String, IOriginsFeatureConfiguration> getChildrenComponent() {
		ImmutableMap.Builder<String, IOriginsFeatureConfiguration> components = ImmutableMap.builder();
		int i = 0;
		for (T entry : entries()) {
			if (entry instanceof IOriginsFeatureConfiguration config)
				components.put(Integer.toString(i), config);
			++i;
		}
		return components.build();
	}
}
