package io.github.apace100.origins.origin;

import io.github.edwinmindcraft.origins.api.OriginsAPI;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Deprecated
public class OriginRegistry {

	private static final Map<io.github.edwinmindcraft.origins.api.origin.Origin, Origin> CACHE_MAP = new ConcurrentHashMap<>();


	@Deprecated
	public static Origin register(Origin origin) {
		return register(origin.getIdentifier(), origin);
	}

	@Deprecated
	public static Origin register(ResourceLocation id, Origin origin) {
		return origin;
	}

	@Deprecated
	protected static Origin update(ResourceLocation id, Origin origin) {
		return register(id, origin);
	}

	public static int size() {
		return OriginsAPI.getOriginsRegistry().keySet().size();
	}

	public static Stream<ResourceLocation> identifiers() {
		return OriginsAPI.getOriginsRegistry().keySet().stream();
	}

	public static Iterable<Map.Entry<ResourceLocation, Origin>> entries() {
		return () -> {
			Iterator<Map.Entry<ResourceKey<io.github.edwinmindcraft.origins.api.origin.Origin>, io.github.edwinmindcraft.origins.api.origin.Origin>> iterator = OriginsAPI.getOriginsRegistry().entrySet().iterator();
			return new Iterator<>() {
				@Override
				public boolean hasNext() {
					return iterator.hasNext();
				}

				@Override
				public Map.Entry<ResourceLocation, Origin> next() {
					Map.Entry<ResourceKey<io.github.edwinmindcraft.origins.api.origin.Origin>, io.github.edwinmindcraft.origins.api.origin.Origin> next = iterator.next();
					return new Map.Entry<>() {
						@Override
						public ResourceLocation getKey() {
							return next.getKey().location();
						}

						@Override
						public Origin getValue() {
							return get(next.getValue());
						}

						@Override
						public Origin setValue(Origin value) {
							return null;
						}
					};
				}
			};
		};
	}

	public static Iterable<Origin> values() {
		return () -> {
			Iterator<io.github.edwinmindcraft.origins.api.origin.Origin> iterator = OriginsAPI.getOriginsRegistry().iterator();
			return new Iterator<>() {
				@Override
				public boolean hasNext() {
					return iterator.hasNext();
				}

				@Override
				public Origin next() {
					return get(iterator.next());
				}
			};
		};
	}

	public static Origin get(ResourceLocation id) {
		return OriginsAPI.getOriginsRegistry().getOptional(id).map(OriginRegistry::get)
				.orElseThrow(() -> new IllegalArgumentException("Could not get origin from id '" + id.toString() + "', as it was not registered!"));
	}

	public static Origin get(io.github.edwinmindcraft.origins.api.origin.Origin origin) {
		return CACHE_MAP.computeIfAbsent(origin, o -> new Origin(() -> o));
	}

	public static boolean contains(ResourceLocation id) {
		return OriginsAPI.getOriginsRegistry().containsKey(id);
	}

	public static boolean contains(Origin origin) {
		return contains(origin.getIdentifier());
	}

	public static void clear() {
		CACHE_MAP.clear();
	}

	public static void reset() {
		clear();
	}
}
