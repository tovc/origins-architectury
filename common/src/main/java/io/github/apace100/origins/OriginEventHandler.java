package io.github.apace100.origins;

import io.github.apace100.origins.api.OriginsAPI;
import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.event.OriginsDynamicRegistryEvent;
import io.github.apace100.origins.api.origin.Origin;
import io.github.apace100.origins.api.origin.OriginLayer;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.registry.OriginsBuiltinRegistries;
import io.github.apace100.origins.api.registry.OriginsDynamicRegistries;
import io.github.apace100.origins.networking.ModPackets;
import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PreventBlockUsePower;
import io.github.apace100.origins.power.PreventItemUsePower;
import io.github.apace100.origins.registry.ModComponentsArchitectury;
import io.github.apace100.origins.registry.ModOrigins;
import io.github.apace100.origins.registry.OriginsDynamicRegistryManager;
import io.netty.buffer.Unpooled;
import me.shedaniel.architectury.event.events.InteractionEvent;
import me.shedaniel.architectury.event.events.LifecycleEvent;
import me.shedaniel.architectury.event.events.PlayerEvent;
import me.shedaniel.architectury.networking.NetworkManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.List;

public class OriginEventHandler {
	public static void register() {
		//Replaces ClientPlayerInteractionManagerMixin & ServerPlayerInteractionManagerMixin#preventBlockInteraction
		InteractionEvent.RIGHT_CLICK_BLOCK.register(OriginEventHandler::preventBlockUse);
		//Replaces ItemStackMixin
		InteractionEvent.RIGHT_CLICK_ITEM.register(OriginEventHandler::preventItemUse);
		//Replaces LoginMixin#openOriginsGui
		PlayerEvent.PLAYER_JOIN.register(OriginEventHandler::playerJoin);
		//Replaces LoginMixin#invokePowerRespawnCallback
		PlayerEvent.PLAYER_RESPAWN.register(OriginEventHandler::respawn);
		LifecycleEvent.SERVER_BEFORE_START.register(OriginsDynamicRegistryManager::addInstance);
		LifecycleEvent.SERVER_STOPPED.register(OriginsDynamicRegistryManager::removeInstance);

		OriginsDynamicRegistryEvent.INITIALIZE_EVENT.register(t -> {
			//Registers builtin registries.
			t.add(OriginsDynamicRegistries.CONFIGURED_POWER_KEY, () -> OriginsBuiltinRegistries.CONFIGURED_POWERS, ConfiguredPower.CODEC);
			t.add(OriginsDynamicRegistries.ORIGIN_KEY, () -> OriginsBuiltinRegistries.ORIGINS, Origin.CODEC);
			t.add(OriginsDynamicRegistries.ORIGIN_LAYER_KEY, () -> OriginsBuiltinRegistries.ORIGIN_LAYERS, OriginLayer.CODEC);
		});
	}

	private static ActionResult preventBlockUse(PlayerEntity player, Hand hand, BlockPos blockPos, Direction direction) {
		if (OriginComponent.getPowers(player, PreventBlockUsePower.class).stream().anyMatch(p -> p.doesPrevent(player.getEntityWorld(), blockPos))) {
			return ActionResult.FAIL;
		}
		return ActionResult.PASS;
	}

	private static TypedActionResult<ItemStack> preventItemUse(PlayerEntity user, Hand hand) {
		if (user != null) {
			OriginComponent component = ModComponentsArchitectury.getOriginComponent(user);
			ItemStack stackInHand = user.getStackInHand(hand);
			for (PreventItemUsePower piup : component.getPowers(PreventItemUsePower.class)) {
				if (piup.doesPrevent(stackInHand)) {
					return TypedActionResult.fail(stackInHand);
				}
			}
			return TypedActionResult.pass(user.getStackInHand(hand));
		}
		return TypedActionResult.pass(ItemStack.EMPTY);
	}

	/**
	 * Replaces {@code LoginMixin.openOriginsGui(ClientConnection, ServerPlayerEntity, CallbackInfo)}
	 */
	private static void playerJoin(ServerPlayerEntity player) {
		OriginComponent component = ModComponentsArchitectury.getOriginComponent(player);

		OriginsAPI.getLayers().stream().filter(x -> x.enabled() && !component.hasOrigin(x))
				.forEach(layer -> component.setOrigin(layer, ModOrigins.EMPTY));

		List<ServerPlayerEntity> playerList = player.getServer().getPlayerManager().getPlayerList();
		playerList.forEach(spe -> ModComponentsArchitectury.syncWith(spe, player));
		ModComponentsArchitectury.syncWith(player, player);
		OriginComponent.sync(player);
		if (!component.hasAllOrigins()) {
			if (component.checkAutoChoosingLayers(player, true)) {
				component.sync();
			}
			if (component.hasAllOrigins()) {
				component.getOrigins().values().forEach(o -> {
					o.getPowerTypes().forEach(powerType -> component.getPower(powerType).onChosen(false));
				});
			} else {
				PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
				data.writeBoolean(true);
				NetworkManager.sendToPlayer(player, ModPackets.OPEN_ORIGIN_SCREEN, data);
			}
		}
	}

	private static void respawn(ServerPlayerEntity serverPlayerEntity, boolean alive) {
		if (!alive) {
			List<Power> powers = ModComponentsArchitectury.getOriginComponent(serverPlayerEntity).getPowers();
			powers.forEach(Power::onRespawn);
		}
	}
}
