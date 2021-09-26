package io.github.edwinmindcraft.origins.common;

import io.github.apace100.origins.power.OriginsPowerTypes;
import io.github.edwinmindcraft.origins.common.registry.OriginRegisters;

public class OriginsCommon {
	public static void initialize() {
		OriginRegisters.register();
		OriginsPowerTypes.register();
	}
}
