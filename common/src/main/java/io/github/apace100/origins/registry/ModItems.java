package io.github.apace100.origins.registry;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.api.OriginsAPI;
import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.origin.OriginLayer;
import io.github.apace100.origins.networking.ModPackets;
import io.netty.buffer.Unpooled;
import me.shedaniel.architectury.networking.NetworkManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ModItems {

	public static final Item ORB_OF_ORIGIN = new Item(new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.RARE)) {
		@Override
		public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
			if (!world.isClient && user instanceof ServerPlayerEntity) { //Avoid fake players (Mainly a forge check)
				OriginComponent component = ModComponentsArchitectury.getOriginComponent(user);

				for (OriginLayer layer : OriginsAPI.getLayers()) {
					if (layer.enabled()) {
						component.setOrigin(layer, ModOrigins.EMPTY);
					}
				}
				component.checkAutoChoosingLayers(user, false);
				component.sync();
				PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
				data.writeBoolean(false);
				NetworkManager.sendToPlayer((ServerPlayerEntity) user, ModPackets.OPEN_ORIGIN_SCREEN, data);
			}
			ItemStack stack = user.getStackInHand(hand);
			if (!user.isCreative()) {
				stack.decrement(1);
			}
			return TypedActionResult.consume(stack);
		}
	};

	public static void register() {
		ModRegistriesArchitectury.ITEMS.register(new Identifier(Origins.MODID, "orb_of_origin"), () -> ORB_OF_ORIGIN);
	}
}
