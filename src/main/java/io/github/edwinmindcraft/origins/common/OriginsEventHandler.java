package io.github.edwinmindcraft.origins.common;

import io.github.apace100.apoli.mixin.EntityAccessor;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.OriginsPowerTypes;
import io.github.apace100.origins.registry.ModDamageSources;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.calio.api.event.CalioDynamicRegistryEvent;
import io.github.edwinmindcraft.calio.api.registry.ICalioDynamicRegistryManager;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import io.github.edwinmindcraft.origins.api.registry.OriginsBuiltinRegistries;
import io.github.edwinmindcraft.origins.api.registry.OriginsDynamicRegistries;
import io.github.edwinmindcraft.origins.common.data.LayerLoader;
import io.github.edwinmindcraft.origins.common.data.OriginLoader;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Mod.EventBusSubscriber(modid = Origins.MODID)
public class OriginsEventHandler {
	//region Reflection
	private static final Method DECREASE_AIR_SUPPLY = ObfuscationReflectionHelper.findMethod(LivingEntity.class, "m_7302_", int.class);
	private static final Method INCREASE_AIR_SUPPLY = ObfuscationReflectionHelper.findMethod(LivingEntity.class, "m_7305_", int.class);

	private static int increaseAirSupply(LivingEntity living, int value) {
		try {
			return (int) INCREASE_AIR_SUPPLY.invoke(living, value);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	private static int decreaseAirSupply(LivingEntity living, int value) {
		try {
			return (int) DECREASE_AIR_SUPPLY.invoke(living, value);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	//endregion

	@SubscribeEvent
	public static void addRegistries(CalioDynamicRegistryEvent.Initialize event) {
		ICalioDynamicRegistryManager registryManager = event.getRegistryManager();
		registryManager.addForge(OriginsDynamicRegistries.ORIGINS_REGISTRY, OriginsBuiltinRegistries.ORIGINS, Origin.CODEC);
		registryManager.addReload(OriginsDynamicRegistries.ORIGINS_REGISTRY, "origins", OriginLoader.INSTANCE);
		registryManager.addValidation(OriginsDynamicRegistries.ORIGINS_REGISTRY, OriginLoader.INSTANCE, Origin.class, ApoliDynamicRegistries.CONFIGURED_POWER_KEY);

		registryManager.add(OriginsDynamicRegistries.LAYERS_REGISTRY, OriginLayer.CODEC);
		registryManager.addReload(OriginsDynamicRegistries.LAYERS_REGISTRY, "origins", LayerLoader.INSTANCE);
		registryManager.addValidation(OriginsDynamicRegistries.LAYERS_REGISTRY, LayerLoader.INSTANCE, OriginLayer.class, OriginsDynamicRegistries.ORIGINS_REGISTRY);
	}

	@SubscribeEvent
	public static void onPlayerTickEnd(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			Player player = event.player;
			if (IPowerContainer.hasPower(player, OriginsPowerTypes.WATER_BREATHING.get())) {
				if (!player.isEyeInFluid(FluidTags.WATER) && !player.hasEffect(MobEffects.WATER_BREATHING) && !player.hasEffect(MobEffects.CONDUIT_POWER)) {
					if (!((EntityAccessor) player).callIsBeingRainedOn()) {
						int landGain = increaseAirSupply(player, 0);
						player.setAirSupply(decreaseAirSupply(player, player.getAirSupply()) - landGain);
						if (player.getAirSupply() == -20) {
							player.setAirSupply(0);

							for (int i = 0; i < 8; ++i) {
								double f = player.getRandom().nextDouble() - player.getRandom().nextDouble();
								double g = player.getRandom().nextDouble() - player.getRandom().nextDouble();
								double h = player.getRandom().nextDouble() - player.getRandom().nextDouble();
								player.level.addParticle(ParticleTypes.BUBBLE, player.getRandomX(0.5), player.getEyeY() + player.getRandom().nextGaussian() * 0.08D, player.getRandomZ(0.5), f * 0.5F, g * 0.5F + 0.25F, h * 0.5F);
							}
							player.hurt(ModDamageSources.NO_WATER_FOR_GILLS, 2.0F);
						}
					} else {
						int landGain = increaseAirSupply(player, 0);
						player.setAirSupply(player.getAirSupply() - landGain);
					}
				} else if (player.getAirSupply() < player.getMaxAirSupply()) {
					player.setAirSupply(increaseAirSupply(player, player.getAirSupply()));
				}
			}
		}
	}
}
