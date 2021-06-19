package io.github.apace100.origins.action.configuration;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.configuration.ConfiguredEntityAction;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public record SpawnEntityConfiguration(EntityType<?> type, CompoundTag tag,
									   ConfiguredEntityAction<?, ?> action) implements IOriginsFeatureConfiguration {

	public static final Codec<SpawnEntityConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.OPTIONAL_ENTITY_TYPE.fieldOf("entity_type").forGetter(x -> Optional.ofNullable(x.type())),
			OriginsCodecs.NBT.optionalFieldOf("tag").forGetter(x -> Optional.ofNullable(x.tag())),
			ConfiguredEntityAction.CODEC.optionalFieldOf("entity_action").forGetter(x -> Optional.ofNullable(x.action()))
	).apply(instance, (t1, t2, t3) -> new SpawnEntityConfiguration(t1.orElse(null), t2.orElse(null), t3.orElse(null))));

	public SpawnEntityConfiguration(@Nullable EntityType<?> type, @Nullable CompoundTag tag, @Nullable ConfiguredEntityAction<?, ?> action) {
		this.type = type;
		this.tag = tag;
		this.action = action;
	}

	@Override
	public @NotNull List<String> getWarnings(@NotNull MinecraftServer server) {
		ImmutableList.Builder<String> builder = ImmutableList.builder();
		if (this.action() != null) builder.addAll(this.action().getWarnings(server).stream().map("SpawnEntity/%s"::formatted).toList());
		if (this.type() == null) builder.add("SpawnEntity/Missing Entity");
		return builder.build();
	}

	@Override
	public @NotNull List<String> getErrors(@NotNull MinecraftServer server) {
		ImmutableList.Builder<String> builder = ImmutableList.builder();
		if (this.action() != null) builder.addAll(this.action().getErrors(server).stream().map("SpawnEntity/%s"::formatted).toList());
		return builder.build();
	}
}
