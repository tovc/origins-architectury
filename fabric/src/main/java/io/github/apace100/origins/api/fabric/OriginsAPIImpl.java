package io.github.apace100.origins.api.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;

public class OriginsAPIImpl {
	public static MinecraftServer currentServer;

	public static MinecraftServer getServer() {
		return currentServer;
	}
}
