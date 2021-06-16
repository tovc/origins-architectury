# Origins Forge: Experimental

Welcome to the experimental branch. This is the branch that will be used for 1.17.

This branch is a major overhaul of most of the underlying systems of the mod with the following goals:
* Phases out `SerializableData` in favor of DataFixerUpper's `Codec`
* Uses something akin to `DynamicRegistryManager` to store powers, origins and layers.
* Data system is now similar to Minecraft's `Feature` & `FeatureConfiguration` combo.
* Provide a semi-stable api that shouldn't have breaking changes during a single minecraft version.
* Provide a way to use `DataGenerator` for powers, origins and layers.

It is also a goal to reimplement a shell of `SerializableData` to allow addon makers to make addons for
both forge and fabric more easily.

Status:
* Doesn't compile.
* Needs to split into Origins / Apoli / Calio like fabric.
* ETA: Don't get your hopes up.