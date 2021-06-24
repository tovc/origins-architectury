package io.github.apace100.origins.networking.packet;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.api.OriginsAPI;
import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.origin.Origin;
import io.github.apace100.origins.api.origin.OriginLayer;
import io.github.apace100.origins.api.network.INetworkHandler;
import io.github.apace100.origins.registry.ModComponentsArchitectury;
import io.github.apace100.origins.registry.ModOrigins;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public record C2SChooseOriginPacket(Identifier layer, Identifier origin) {
	public static C2SChooseOriginPacket decode(PacketByteBuf buffer) {
		Identifier layer = buffer.readIdentifier();
		boolean isRandom = buffer.readBoolean();
		Identifier origin = isRandom ? null : buffer.readIdentifier();
		return new C2SChooseOriginPacket(layer, origin);
	}

	public C2SChooseOriginPacket(Identifier layer, @Nullable Identifier origin) {
		this.layer = layer;
		this.origin = origin;
	}

	public void encode(PacketByteBuf buffer) {
		buffer.writeIdentifier(this.layer());
		buffer.writeBoolean(this.origin() == null);
		if (this.origin() != null)
			buffer.writeIdentifier(this.origin());
	}

	public void handle(INetworkHandler networkHandler) {
		PlayerEntity player = networkHandler.getPlayer();
		networkHandler.queue(() -> {
			OriginComponent component = ModComponentsArchitectury.getOriginComponent(player);
			OriginLayer layer = OriginsAPI.getLayers().get(this.layer());
			if (!component.hasAllOrigins() && !component.hasOrigin(layer)) {
				Identifier originIdentifier = this.origin();
				if (originIdentifier == null) {
					List<Identifier> randomOrigins = layer.randomOrigins(player).toList();
					if (layer.allowRandom() && randomOrigins.size() > 0) {
						originIdentifier = randomOrigins.get(new Random().nextInt(randomOrigins.size()));
						Origins.LOGGER.info("Player {} was randomly assigned the following Origin: {}, for layer: {}", player.getDisplayName().asString(), originIdentifier, this.layer());
					} else {
						Origins.LOGGER.info("Player {} tried to choose a random Origin for layer {}, which is not allowed!", player.getDisplayName().asString(), this.layer());
						component.setOrigin(layer, ModOrigins.EMPTY);
					}
				}
				Origin origin = OriginsAPI.getOrigins().get(originIdentifier);
				if (origin == null) {
					Origins.LOGGER.warn("Player {} chose unknown origin: {}", player.getDisplayName().asString(), this.origin());
					return;
				}

				if (!origin.choosable() || !layer.contains(originIdentifier, player)) {
					Origins.LOGGER.info("Player {} chose Origin: {}, for layer: {}", player.getDisplayName().asString(), this.origin(), this.layer());
					return;
				}
				boolean hadOriginBefore = component.hadOriginBefore();
				boolean hadAllOrigins = component.hasAllOrigins();
				component.setOrigin(layer, origin);
				component.checkAutoChoosingLayers(player, false);
				component.sync();
				if (component.hasAllOrigins() && !hadAllOrigins)
					component.getOrigins().values().forEach(o -> o.powers().forEach(powerType -> component.getPower(powerType).onChosen(player, hadOriginBefore)));
				S2CConfirmOriginPacket.forPlayer((ServerPlayerEntity) player, this.layer(), originIdentifier);
				component.sync();
			} else {
				Origins.LOGGER.warn("Player {} tried to choose origin for layer {} while having one already.", player.getDisplayName().asString(), this.layer());
			}
		});
		networkHandler.setHandled(true);
	}
}
