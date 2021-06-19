package io.github.apace100.origins.networking.packet;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.OriginsForge;
import io.github.apace100.origins.api.IOriginsDynamicRegistryManager;
import io.github.apace100.origins.api.origin.Origin;
import io.github.apace100.origins.api.origin.OriginLayer;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.registry.OriginsDynamicRegistries;
import io.github.apace100.origins.integration.OriginEventsArchitectury;
import io.github.apace100.origins.origin.OriginLayers;
import io.github.apace100.origins.origin.OriginRegistry;
import io.github.apace100.origins.power.PowerType;
import io.github.apace100.origins.power.PowerTypeRegistry;
import io.github.apace100.origins.factory.PowerFactory;
import io.github.apace100.origins.registry.ModRegistriesArchitectury;
import io.github.apace100.origins.util.SerializableData;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class DynamicRegistryMessage implements IntSupplier {
	public static DynamicRegistryMessage decode(PacketByteBuf buf) {
		int size = buf.readVarInt();
		Origins.LOGGER.debug("Server sent {} powers", size);
		Map<Identifier, PowerType<?>> powers = new HashMap<>(size);
		for (int i = 0; i < size; i++) {
			if (!buf.readBoolean()) continue;
			Identifier powerId = buf.readIdentifier();
			Identifier factoryId = buf.readIdentifier();
			PowerFactory<?> factory = ModRegistriesArchitectury.POWER_FACTORY.get(factoryId);
			PowerFactory<?>.Instance factoryInstance = factory.read(buf);
			PowerType<?> type = new PowerType<>(powerId, factoryInstance);
			type.setTranslationKeys(buf.readString(), buf.readString());
			if (buf.readBoolean()) type.setHidden();
			powers.put(powerId, type);
		}
		size = buf.readVarInt();
		Origins.LOGGER.debug("Server sent {} conditionedOrigins", size);
		Map<Identifier, SerializableData.Instance> origins = new HashMap<>(size);
		for (int i = 0; i < size; i++) {
			origins.put(buf.readIdentifier(), Origin.DATA.read(buf));
		}
		size = buf.readVarInt();
		Origins.LOGGER.debug("Server sent {} layers", size);
		List<OriginLayer> layers = new ArrayList<>(size);
		for (int i = 0; i < size; i++) layers.add(OriginLayer.read(buf));
		return new DynamicRegistryMessage(powers, origins, layers);
	}

	//Those three need to be initialized sequentially, as such there is no reason to send 3 separate packets.
	//Origins are required for layers, and powers are required for conditionedOrigins.
	private final Map<Identifier, PowerType<?>> powers;
	private final Map<Identifier, Origin> origins;
	private final List<OriginLayer> layers;
	private int loginIndex;

	public DynamicRegistryMessage(Map<Identifier, PowerType<?>> powers, Map<Identifier, Origin> origins, List<OriginLayer> layers) {
		this.powers = ImmutableMap.copyOf(powers);
		this.origins = ImmutableMap.copyOf(origins);
		this.layers = ImmutableList.copyOf(layers);
	}

	public void encode(PacketByteBuf buf) {
		//Counts will rarely if ever need 4 bytes to be encoded.
		buf.writeVarInt(this.powers.size());
		this.powers.forEach((key, type) -> {
			PowerFactory<?>.Instance factory = type.getFactory();
			buf.writeBoolean(factory != null);
			if (factory != null) {
				buf.writeIdentifier(key);
				factory.write(buf);
				buf.writeString(type.getOrCreateNameTranslationKey());
				buf.writeString(type.getOrCreateDescriptionTranslationKey());
				buf.writeBoolean(type.isHidden());
			}
		});

		buf.writeVarInt(this.origins.size());
		this.origins.forEach((key, value) -> {
			buf.writeIdentifier(key);
			Origin.DATA.write(buf, value);
		});

		buf.writeVarInt(this.layers.size());
		this.layers.forEach(layer -> layer.write(buf));
	}

	public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			IOriginsDynamicRegistryManager clientManager = OriginsDynamicRegistries.get(null);
			if (clientManager == null)
				throw new NullPointerException();
			MutableRegistry<ConfiguredPower<?>> powers = clientManager.get(OriginsDynamicRegistries.CONFIGURED_POWER_KEY);
			MutableRegistry<io.github.apace100.origins.api.origin.OriginLayer> layers = clientManager.get(OriginsDynamicRegistries.ORIGIN_LAYER_KEY);
			MutableRegistry<Origin> origins = clientManager.get(OriginsDynamicRegistries.ORIGIN_KEY);
			PowerTypeRegistry.clear();
			OriginRegistry.reset();
			OriginLayers.clear();

			this.powers.forEach(PowerTypeRegistry::register);
			Origins.LOGGER.debug("Loaded {} powers from server on client", this.powers.size());
			OriginEventsArchitectury.POWER_TYPES_LOADED.invoker().onDataLoaded(true);

			this.origins.forEach((id, data) -> OriginRegistry.register(id, Origin.createFromData(id, data)));
			Origins.LOGGER.debug("Loaded {} conditionedOrigins from server on client", this.origins.size());
			OriginEventsArchitectury.ORIGINS_LOADED.invoker().onDataLoaded(true);

			this.layers.forEach(OriginLayers::add);
			Origins.LOGGER.debug("Loaded {} layers from server on client", this.layers.size());
			OriginEventsArchitectury.ORIGIN_LAYERS_LOADED.invoker().onDataLoaded(true);
		});
		OriginsForge.channel.reply(new AcknowledgeMessage(), contextSupplier.get());
		contextSupplier.get().setPacketHandled(true);
	}

	public int getLoginIndex() {
		return loginIndex;
	}

	public void setLoginIndex(int loginIndex) {
		this.loginIndex = loginIndex;
	}

	@Override
	public int getAsInt() {
		return this.loginIndex;
	}
}
