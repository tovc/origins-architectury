package io.github.apace100.origins.condition.meta;

import io.github.apace100.origins.api.IOriginsFeatureConfiguration;

public interface IDelegatedConditionConfiguration<V> extends IOriginsFeatureConfiguration {
	boolean check(V parameters);
}
