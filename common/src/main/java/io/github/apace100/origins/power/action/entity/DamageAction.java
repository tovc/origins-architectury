package io.github.apace100.origins.power.action.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

import java.util.function.Consumer;

public class DamageAction implements Consumer<Entity> {

	public static final Codec<DamageAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.DAMAGE_SOURCE.fieldOf("source").forGetter(x -> x.source),
			Codec.FLOAT.fieldOf("amount").forGetter(x -> x.amount)
	).apply(instance, DamageAction::new));

	private final DamageSource source;
	private final float amount;

	public DamageAction(DamageSource source, float amount) {
		this.source = source;
		this.amount = amount;
	}

	@Override
	public void accept(Entity entity) {
		if (entity instanceof LivingEntity) entity.damage(source, amount);
	}
}
