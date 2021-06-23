package io.github.apace100.origins;

import io.github.apace100.origins.api.OriginsAPI;
import io.github.apace100.origins.command.*;
import io.github.apace100.origins.data.OriginLayerLoader;
import io.github.apace100.origins.data.OriginLoader;
import io.github.apace100.origins.data.PowerLoader;
import io.github.apace100.origins.networking.ModPacketsC2S;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.registry.*;
import io.github.apace100.origins.registry.action.ModBlockActions;
import io.github.apace100.origins.registry.action.ModEntityActions;
import io.github.apace100.origins.registry.action.ModItemActions;
import io.github.apace100.origins.registry.condition.*;
import io.github.apace100.origins.util.ChoseOriginCriterion;
import io.github.apace100.origins.util.GainedPowerCriterion;
import io.github.apace100.origins.util.OriginsConfigSerializer;
import me.shedaniel.architectury.event.events.CommandRegistrationEvent;
import me.shedaniel.architectury.platform.Platform;
import me.shedaniel.architectury.registry.CriteriaTriggersRegistry;
import me.shedaniel.architectury.registry.ReloadListeners;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Origins {

	public static final String MODID = OriginsAPI.MODID;
	public static final Logger LOGGER = LogManager.getLogger(Origins.class);

	public static void register() {
		String[] splitVersion = VERSION.split("\\.");
		SEMVER = new int[splitVersion.length];
		for (int i = 0; i < SEMVER.length; i++) {
			SEMVER[i] = Integer.parseInt(splitVersion[i]);
		}
		LOGGER.info("Origins " + VERSION + " is initializing. Have fun!");
		AutoConfig.register(ServerConfig.class, OriginsConfigSerializer::new);
		config = AutoConfig.getConfigHolder(ServerConfig.class).getConfig();

		ModBlocks.register();
		ModItems.register();
		ModTags.register();
		ModPacketsC2S.register();
		ModEnchantments.register();
		ModEntities.register();
		ModLoot.registerLootTables();
		ModRecipes.register();
		ModPowers.register();
		ModEntityConditions.register();
		ModItemConditions.register();
		ModBlockConditions.register();
		ModDamageConditions.register();
		ModFluidConditions.register();
		ModBiomeConditions.register();
		ModEntityActions.register();
		ModItemActions.register();
		ModBlockActions.register();
		Origin.init();
		OriginEventHandler.register();
		CommandRegistrationEvent.EVENT.register((dispatcher, dedicated) -> {
			OriginCommand.register(dispatcher);
			ResourceCommand.register(dispatcher);
		});
		CriteriaTriggersRegistry.register(ChoseOriginCriterion.INSTANCE);
		CriteriaTriggersRegistry.register(GainedPowerCriterion.INSTANCE);
		ArgumentTypes.register("origins:origin", OriginArgument.class, new ConstantArgumentSerializer<>(OriginArgument::origin));
		ArgumentTypes.register("origins:layer", LayerArgument.class, new ConstantArgumentSerializer<>(LayerArgument::layer));
		ArgumentTypes.register("origins:power", PowerArgument.class, new ConstantArgumentSerializer<>(PowerArgument::power));
		ArgumentTypes.register("origins:power_operation", PowerOperation.class, new ConstantArgumentSerializer<>(PowerOperation::operation));
		ReloadListeners.registerReloadListener(ResourceType.SERVER_DATA, new PowerLoader());
		ReloadListeners.registerReloadListener(ResourceType.SERVER_DATA, new OriginLoader());
		ReloadListeners.registerReloadListener(ResourceType.SERVER_DATA, new OriginLayerLoader());
		//FIXME Apply validation steps.
	}

	public static Identifier identifier(String path) {
		return new Identifier(Origins.MODID, path);
	}
	public static String VERSION = "";
	public static int[] SEMVER;
	public static ServerConfig config;

	@Config(name = Origins.MODID + "_server")
	public static class ServerConfig implements ConfigData {
		public boolean performVersionCheck = true;
		/**
		 * Ability to disable food restrictions.
		 * Currently supporting just diet by default.
		 * Might add spice of life.
		 */
		public boolean disableFoodRestrictions = Platform.isModLoaded("diet");
	}
}
