package io.github.apace100.origins.networking;

import net.minecraft.network.PacketByteBuf;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.ObjIntConsumer;
import java.util.function.ToIntFunction;

public record MessageDefinition<MSG>(NetworkChannel channel, int index, Class<MSG> message,
									 BiConsumer<MSG, PacketByteBuf> encoder,
									 Function<PacketByteBuf, MSG> decoder,
									 BiConsumer<MSG, INetworkHandler> handler,
									 @Nullable ToIntFunction<MSG> loginGetter,
									 @Nullable ObjIntConsumer<MSG> loginSetter,
									 @Nullable Function<Boolean, List<Pair<String, MSG>>> loginMessages,
									 PacketDirection direction) {
}
