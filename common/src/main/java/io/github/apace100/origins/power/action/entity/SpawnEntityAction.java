package io.github.apace100.origins.power.action.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.power.factory.action.ActionFactory;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;

import java.util.Optional;
import java.util.function.Consumer;

public class SpawnEntityAction implements Consumer<Entity> {

	public static final Codec<SpawnEntityAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.OPTIONAL_ENTITY_TYPE.fieldOf("entity_type").forGetter(x -> x.type),
			CompoundTag.CODEC.optionalFieldOf("tag").forGetter(x -> x.tag),
			OriginsCodecs.ENTITY_ACTION.optionalFieldOf("entity_action").forGetter(x -> x.action)
	).apply(instance, SpawnEntityAction::new));

	private final Optional<EntityType<?>> type;
	private final Optional<CompoundTag> tag;
	private final Optional<ActionFactory.Instance<Entity>> action;

	public SpawnEntityAction(Optional<EntityType<?>> type, Optional<CompoundTag> tag, Optional<ActionFactory.Instance<Entity>> action) {
		this.type = type;
		this.tag = tag;
		this.action = action;
	}

	@Override
	public void accept(Entity entity) {
		this.type.map(x -> x.create(entity.getEntityWorld())).ifPresent(e -> {
			this.tag.ifPresent(tag -> {
				CompoundTag compoundTag = e.toTag(new CompoundTag());
				compoundTag.copyFrom(tag);
				e.fromTag(compoundTag);
			});
			entity.getEntityWorld().spawnEntity(e);
			this.action.ifPresent(x -> x.accept(e));
		});
	}
}
