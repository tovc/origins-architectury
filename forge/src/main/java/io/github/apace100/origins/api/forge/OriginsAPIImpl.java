package io.github.apace100.origins.api.forge;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class OriginsAPIImpl {
	public static MinecraftServer getServer() {
		return EffectiveSide.get().isServer() ? ServerLifecycleHooks.getCurrentServer() : null;
	}
}
