package io.github.apace100.origins;

import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.registry.forge.ModComponentsArchitecturyImpl;
import me.shedaniel.architectury.platform.forge.EventBuses;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.maven.artifact.versioning.ArtifactVersion;

import java.util.Arrays;

@Mod(Origins.MODID)
public class OriginsForge {

	//This channel is used for version checking.
	//It won't allow connection if the channels mismatch.
	/**
	 * Lowest version without networking changes.
	 */
	private static final String NETVERSION = "0.7.0";
	public static final SimpleChannel channel = NetworkRegistry.newSimpleChannel(Origins.identifier("handshake"), () -> NETVERSION, NetworkRegistry.acceptMissingOr(NETVERSION), NetworkRegistry.acceptMissingOr(NETVERSION));

	public OriginsForge() {
		ArtifactVersion version = ModLoadingContext.get().getActiveContainer().getModInfo().getVersion();
		Origins.VERSION = version.toString();
		EventBuses.registerModEventBus(Origins.MODID, FMLJavaModLoadingContext.get().getModEventBus());
		Origins.register();
		DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> OriginsForgeClient::initialize);
		DistExecutor.safeRunWhenOn(Dist.DEDICATED_SERVER, () -> OriginsServer::register);
		FMLJavaModLoadingContext.get().getModEventBus().addListener((FMLCommonSetupEvent event) -> CapabilityManager.INSTANCE.register(OriginComponent.class, new ModComponentsArchitecturyImpl.OriginStorage(), () -> null));
		MinecraftForge.EVENT_BUS.addListener((FMLServerAboutToStartEvent event) -> OriginsClient.isServerRunningOrigins = true);
		MinecraftForge.EVENT_BUS.addListener((FMLServerStoppedEvent event) -> OriginsClient.isServerRunningOrigins = false);
	}

	private static boolean isRemoteValid(String remote) {
		if (NetworkRegistry.ABSENT.equals(remote))
			return true;
		if (NetworkRegistry.ACCEPTVANILLA.equals(remote))
			return true;
		try {
			int[] ints = Arrays.stream(remote.split("\\.")).mapToInt(Integer::parseInt).toArray();
			if (ints.length == 0) {
				Origins.LOGGER.warn("Remote version couldn't be established: {}", remote);
				return false;
			}
			if (ints.length == 1)
				return Origins.SEMVER.length == 1 && ints[0] == Origins.SEMVER[0];
			boolean minor = ints[0] == Origins.SEMVER[0] && ints[1] == Origins.SEMVER[2];
			boolean build = true;
			return minor && build;
		} catch (NumberFormatException e) {
			Origins.LOGGER.warn("Remote isn't using SemVer, probably a newer version: {}", remote);
			return false;
		}
	}
}
