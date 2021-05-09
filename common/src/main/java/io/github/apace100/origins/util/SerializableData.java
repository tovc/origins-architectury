package io.github.apace100.origins.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import javax.swing.text.html.Option;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class SerializableData {

    private HashMap<String, Entry<?>> dataFields = new HashMap<>();

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
        dataFields.forEach((name, entry) -> {
            boolean isPresent = instance.get(name) != null;
            if(entry.hasDefault && entry.defaultValue == null) {
                buffer.writeBoolean(isPresent);
            }
            if(isPresent) {
                entry.dataType.send(buffer, instance.get(name));
            }
        });
    }

    public Instance read(PacketByteBuf buffer) {
        Instance instance = new Instance();
        dataFields.forEach((name, entry) -> {
            boolean isPresent = true;
            if(entry.hasDefault && entry.defaultValue == null) {
                isPresent = buffer.readBoolean();
            }
            instance.set(name, isPresent ? entry.dataType.receive(buffer) : null);
        });
        return instance;
    }

    public Instance read(JsonObject jsonObject) {
        Instance instance = new Instance();
        try {
            dataFields.forEach((name, entry) -> {
                if(!jsonObject.has(name)) {
                    if(entry.hasDefault()) {
                        instance.set(name, entry.getDefault(instance));
                    } else {
                        throw new JsonSyntaxException("JSON requires field: " + name);
                    }
                } else {
                    instance.set(name, entry.dataType.read(jsonObject.get(name)));
                }
            });
        } catch(JsonParseException | ClassCastException e) {
            throw new JsonSyntaxException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
        return instance;
    }

    public <T> Optional<Codec<T>> tryMakeCodec (Function<Instance, T> to, BiFunction<SerializableData, T, Instance> from) {
        return dataCodec().map(codec -> codec.xmap(to, x -> from.apply(this, x)));
    }

    public Optional<Codec<SerializableData.Instance>> dataCodec() {
        if (this.dataFields.values().stream().map(x -> x.dataType).anyMatch(x -> !x.hasCodec()))
            return Optional.empty();
        return Optional.of(new SerializableDataCodec(this));
    }

    private static class SerializableDataCodec implements Codec<SerializableData.Instance> {

        private SerializableData container;

        private SerializableDataCodec(SerializableData container) {
            this.container = container;
        }

        @Override
        public <T> DataResult<T> encode(Instance input, DynamicOps<T> ops, T prefix) {
            RecordBuilder<T> map = ops.mapBuilder();
            for (Map.Entry<String, Entry<?>> entry : this.container.dataFields.entrySet()) {
                String name = entry.getKey();
                Entry<?> encoder = entry.getValue();
                DataResult<T> encode = encoder.encode(input, input.getOrEmpty(name), ops, prefix);
                if (encode.result().map(ops.empty()::equals).orElse(false))
                    continue;
                if (encode.error().isPresent()) {
                    map.withErrorsFrom(encode);
                    return map.build(prefix);
                }
                map.add(name, encode);
            }
            return map.build(prefix);
        }

        @Override
        public <T> DataResult<Pair<Instance, T>> decode(DynamicOps<T> ops, T input) {
            DataResult<MapLike<T>> map = ops.getMap(input);
            if (map.error().isPresent())
                return DataResult.error(map.error().get().message());
            return map.flatMap(data -> {
                Instance instance = container.new Instance();
                for (Map.Entry<String, Entry<?>> entry : this.container.dataFields.entrySet()) {
                    Entry<?> decoder = entry.getValue();
                    T d = data.get(entry.getKey());
                    if (d == null && !decoder.hasDefault())
                        return DataResult.error("Missing required field: " + entry.getKey());
                    DataResult<Pair<Optional<?>, T>> decode = ((Entry) decoder).decode(instance, ops, input);
                    if (decode.error().isPresent())
                        return DataResult.error(decode.error().get().message());
                    Optional<Object> value = decode.result().flatMap(Pair::getFirst);
                    if (!value.isPresent() && !decoder.hasDefault())
                        return DataResult.error("Failed to deserialize required field: " + entry.getKey());
                    instance.data.put(entry.getKey(), value.orElseGet(() -> decoder.getDefault(instance)));
                }
                return DataResult.success(Pair.of(instance, input));
            });
        }
    }

    public class Instance {
        private HashMap<String, Object> data = new HashMap<>();

        public Instance() {

        }

        public boolean isPresent(String name) {
            if(dataFields.containsKey(name)) {
                Entry<?> entry = dataFields.get(name);
                if(entry.hasDefault && entry.defaultValue == null) {
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
            if(!data.containsKey(name)) {
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

        /** @deprecated use {@link #get(String)} instead */
        @Deprecated
        public String getString(String name) {
            return get(name);
        }

        /** @deprecated use {@link #get(String)} instead */
        @Deprecated
        public Identifier getId(String name) {
            return get(name);
        }

        /** @deprecated use {@link #get(String)} instead */
        @Deprecated
        public EntityAttributeModifier getModifier(String name) {
            return get(name);
        }
    }

    private static class Entry<T> {
        public final SerializableDataType<T> dataType;
        public final T defaultValue;
        private final Function<Instance, T> defaultFunction;
        private final boolean hasDefault;
        private final boolean hasDefaultFunction;

        public Entry(SerializableDataType<T> dataType) {
            this.dataType = dataType;
            this.defaultValue = null;
            this.defaultFunction = null;
            this.hasDefault = false;
            this.hasDefaultFunction = false;
        }

        public Entry(SerializableDataType<T> dataType, T defaultValue) {
            this.dataType = dataType;
            this.defaultValue = defaultValue;
            this.defaultFunction = null;
            this.hasDefault = true;
            this.hasDefaultFunction = false;
        }

        public Entry(SerializableDataType<T> dataType, Function<Instance, T> defaultFunction) {
            this.dataType = dataType;
            this.defaultValue = null;
            this.defaultFunction = defaultFunction;
            this.hasDefault = false;
            this.hasDefaultFunction = true;
        }

        public boolean hasDefault() {
            return hasDefault || hasDefaultFunction;
        }

        public T getDefault(Instance dataInstance) {
            if(hasDefaultFunction) {
                return defaultFunction.apply(dataInstance);
            } else if(hasDefault) {
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
            }).orElseGet(() -> DataResult.error("Missing codec"));
        }

        public <T1> DataResult<T1> encode(Instance instance, Optional<T> input, DynamicOps<T1> ops, T1 prefix) {
            return this.dataType.getCodec().map(x -> input.map(t -> {
                DataResult<T1> encode = x.encode(t, ops, prefix);
                if (encode.error().isPresent() && this.hasDefault())
                    return x.encode(this.getDefault(instance), ops, prefix);
                return encode;
            }).orElseGet(() -> DataResult.success(ops.empty()))).orElseGet(() -> DataResult.error("Missing codec"));
        }
    }
}
