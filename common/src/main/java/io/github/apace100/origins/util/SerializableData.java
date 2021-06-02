package io.github.apace100.origins.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

public class SerializableData extends MapCodec<SerializableData.Instance> {

	private final HashMap<String, Entry<?>> dataFields = new HashMap<>();

	public SerializableData add(String name, SerializableDataType<?> type) {
		dataFields.put(name, new Entry<>(type));
		return this;
	}

	public <T> SerializableData add(String name, SerializableDataType<T> type, T defaultValue) {
		dataFields.put(name, new Entry<>(type, defaultValue));
		return this;
	}

	public <T> SerializableData addFunctionedDefault(String name, SerializableDataType<T> type, Function<Instance, T> defaultFunction) {
		dataFields.put(name, new Entry<>(type, defaultFunction));
		return this;
	}

	public void write(PacketByteBuf buffer, Instance instance) {
		try {
			buffer.encode(this.codec(), instance);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Instance read(PacketByteBuf buffer) {
		try {
			return buffer.decode(this.codec());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Instance read(JsonObject jsonObject) {
		DataResult<Pair<Instance, JsonElement>> decode = this.codec().decode(JsonOps.INSTANCE, jsonObject);
		if (decode.error().isPresent())
			throw new JsonSyntaxException(decode.error().map(DataResult.PartialResult::message).orElse("No error message"));
		return decode.result().map(Pair::getFirst).orElseThrow(RuntimeException::new);
	}

	public Optional<Codec<SerializableData.Instance>> tryGetCodec() {
		return Optional.of(this.codec()).filter(x -> this.dataFields.values().stream().allMatch(t -> t.dataType.hasCodec()));
	}


	public <T> Optional<Codec<T>> tryMakeCodec(Function<Instance, T> toInstance, BiFunction<SerializableData, T, Instance> toData) {
		return this.tryGetCodec().map(c -> c.xmap(toInstance, f -> toData.apply(this, f)));
	}

	@Override
	public <T> Stream<T> keys(DynamicOps<T> ops) {
		return this.dataFields.keySet().stream().map(ops::createString);
	}

	@Override
	public <T> DataResult<Instance> decode(DynamicOps<T> ops, MapLike<T> input) {
		Instance instance = new Instance();
		Set<String> missingFields = new HashSet<>();
		Map<String, String> errors = new HashMap<>();
		for (Map.Entry<String, Entry<?>> stringEntryEntry : this.dataFields.entrySet()) {
			String name = stringEntryEntry.getKey();
			Entry<?> entry = stringEntryEntry.getValue();
			T t = input.get(name);
			if (t == null || Objects.equals(t, ops.empty())) {
				if (entry.hasDefault())
					instance.set(name, entry.getDefault(instance));
				else
					missingFields.add(name);
			} else {
				Either<Optional<?>, String> either = entry.decode(instance, ops, t).get().mapBoth(Pair::getFirst, DataResult.PartialResult::message);
				either.ifLeft(a -> a.ifPresent(b -> instance.set(name, b)));
				either.ifRight(a -> errors.put(name, a));
			}
		}
		StringBuilder errorMessage = new StringBuilder();
		if (!missingFields.isEmpty()) {
			errorMessage.append("Missing fields: [").append(String.join(",", missingFields)).append(']');
		}
		for (Map.Entry<String, String> entry : errors.entrySet()) {
			String name = entry.getKey();
			String error = entry.getValue();
			if (errorMessage.length() > 0) errorMessage.append('\n');
			errorMessage.append("Variable \"").append(name).append("\" failed with error: [").append(error).append("]");
		}
		return errorMessage.length() > 0 ? DataResult.error(errorMessage.toString(), instance) : DataResult.success(instance);
	}

	@Override
	public <T> RecordBuilder<T> encode(Instance input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
		for (Map.Entry<String, Entry<?>> entry : dataFields.entrySet()) {
			String name = entry.getKey();
			Entry<?> value = entry.getValue();
			if (input.isPresent(name))
				prefix.add(name, value.encode(input, name, ops, ops.empty()));
		}
		return prefix;
	}

	private static class Entry<T> {
		public final SerializableDataType<T> dataType;
		public final T defaultValue;
		private final Function<Instance, T> defaultFunction;
		private final boolean hasDefault;

		public Entry(SerializableDataType<T> dataType) {
			this.dataType = dataType;
			this.defaultValue = null;
			this.defaultFunction = null;
			this.hasDefault = false;
		}

		public Entry(SerializableDataType<T> dataType, T defaultValue) {
			this.dataType = dataType;
			this.defaultValue = defaultValue;
			this.defaultFunction = null;
			this.hasDefault = true;
		}

		public Entry(SerializableDataType<T> dataType, Function<Instance, T> defaultFunction) {
			this.dataType = dataType;
			this.defaultValue = null;
			this.defaultFunction = defaultFunction;
			this.hasDefault = false;
		}

		public boolean hasDefault() {
			return hasDefault || defaultFunction != null;
		}

		public T getDefault(Instance dataInstance) {
			if (defaultFunction != null) {
				return defaultFunction.apply(dataInstance);
			} else if (hasDefault) {
				return defaultValue;
			} else {
				throw new IllegalStateException("Tried to access default value of serializable data entry, when no default was provided.");
			}
		}

		public <T1> DataResult<Pair<Optional<T>, T1>> decode(Instance instance, DynamicOps<T1> ops, T1 input) {
			return this.dataType.getCodec().map(x -> {
				DataResult<Pair<T, T1>> decode = x.decode(ops, input);
				if (decode.error().isPresent() && this.hasDefault())
					return DataResult.success(Pair.of(Optional.of(this.getDefault(instance)), input));
				return decode.map(t -> t.mapFirst(Optional::ofNullable));
			}).orElseGet(() -> {
				if (ops instanceof JsonOps && !ops.compressMaps()) {
					try {
						return DataResult.success(Pair.of(Optional.ofNullable(this.dataType.read((JsonElement) input)), ops.empty()));
					} catch (Throwable t) {
						return DataResult.error("Failed to read json: " + t.getMessage());
					}
				}
				return ops.getByteBuffer(input).flatMap(buffer -> {
					PacketByteBuf buf = new PacketByteBuf(Unpooled.copiedBuffer(buffer));
					try {
						T receive = this.dataType.receive(buf);
						return DataResult.success(Pair.of(Optional.ofNullable(receive), input));
					} catch (Throwable t) {
						return DataResult.error("Failed to read buffer: " + t.getMessage());
					} finally {
						buf.release();
					}
				});
			});
		}

		public <T1> DataResult<T1> encode(Instance instance, String name, DynamicOps<T1> ops, T1 prefix) {
			Optional<T> input = instance.getOrEmpty(name);
			return this.dataType.getCodec().map(x -> input.map(t -> {
				DataResult<T1> encode = x.encode(t, ops, prefix);
				if (encode.error().isPresent() && this.hasDefault())
					return x.encode(this.getDefault(instance), ops, prefix);
				return encode;
			}).orElseGet(() -> DataResult.success(ops.empty()))).orElseGet(() -> {
				if (!input.isPresent())
					return this.hasDefault() ? DataResult.success(ops.empty()) : DataResult.error("Non defaulted field: " + name);
				PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
				try {
					this.dataType.send(buffer, input.get());
					T1 result = ops.createByteList(buffer.nioBuffer());
					return DataResult.success(result);
				} catch (Throwable t) {
					return DataResult.error("Failed to write buffer: " + t.getMessage());
				} finally {
					buffer.release();
				}
			});
		}
	}

	public class Instance {
		private final HashMap<String, Object> data = new HashMap<>();

		public Instance() {

		}

		public boolean isPresent(String name) {
			if (dataFields.containsKey(name)) {
				Entry<?> entry = dataFields.get(name);
				if (entry.hasDefault && entry.defaultValue == null) {
					return get(name) != null;
				}
			}
			return true;
		}

		public void set(String name, Object value) {
			this.data.put(name, value);
		}

		@SuppressWarnings("unchecked")
		public <T> Optional<T> getOrEmpty(String name) {
			if (!data.containsKey(name))
				return Optional.empty();
			return Optional.ofNullable((T) data.get(name));
		}

		@SuppressWarnings("unchecked")
		public <T> T get(String name) {
			if (!data.containsKey(name)) {
				throw new RuntimeException("Tried to get field \"" + name + "\" from data, which did not exist.");
			}
			return (T) data.get(name);
		}

		public int getInt(String name) {
			return get(name);
		}

		public boolean getBoolean(String name) {
			return get(name);
		}

		public float getFloat(String name) {
			return get(name);
		}

		public double getDouble(String name) {
			return get(name);
		}

		/**
		 * @deprecated use {@link #get(String)} instead
		 */
		@Deprecated
		public String getString(String name) {
			return get(name);
		}

		/**
		 * @deprecated use {@link #get(String)} instead
		 */
		@Deprecated
		public Identifier getId(String name) {
			return get(name);
		}

		/**
		 * @deprecated use {@link #get(String)} instead
		 */
		@Deprecated
		public EntityAttributeModifier getModifier(String name) {
			return get(name);
		}
	}
}
