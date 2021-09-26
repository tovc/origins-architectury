package io.github.edwinmindcraft.origins.api.registry;

import io.github.edwinmindcraft.origins.api.origin.Origin;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Supplier;

public class OriginsBuiltinRegistries {
	public static Supplier<IForgeRegistry<Origin>> ORIGINS;
}
