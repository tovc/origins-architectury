package io.github.apace100.origins.registry;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.networking.ModPackets;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayer;
import io.github.apace100.origins.origin.OriginLayers;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class ModItems {

    public static final Item ORB_OF_ORIGIN = new Item(new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC).rarity(Rarity.RARE)) {
        @Override
        public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
            if(!world.isClientSide) {
                OriginComponent component = ModComponents.ORIGIN.get(user);
                for (OriginLayer layer : OriginLayers.getLayers()) {
                    if(layer.isEnabled()) {
                        component.setOrigin(layer, Origin.EMPTY);
                    }
                }
                component.checkAutoChoosingLayers(user, false);
                component.sync();
                FriendlyByteBuf data = new FriendlyByteBuf(Unpooled.buffer());
                data.writeBoolean(false);
                ServerSidePacketRegistry.INSTANCE.sendToPlayer(user, ModPackets.OPEN_ORIGIN_SCREEN, data);
            }
            ItemStack stack = user.getItemInHand(hand);
            if(!user.isCreative()) {
                stack.shrink(1);
            }
            return InteractionResultHolder.consume(stack);
        }
    };

    public static void register() {
        Registry.register(Registry.ITEM, new ResourceLocation(Origins.MODID, "orb_of_origin"), ORB_OF_ORIGIN);
    }
}
