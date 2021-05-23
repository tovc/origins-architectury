package io.github.apace100.origins.power.action.entity;

import com.mojang.serialization.Codec;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

import java.util.function.Consumer;

public class GainAirAction implements Consumer<Entity> {
	public static final Codec<GainAirAction> CODEC = Codec.INT.xmap(GainAirAction::new, x -> x.value);

	private final int value;

	public GainAirAction(int value) {this.value = value;}

	@Override
	public void accept(Entity entity) {
		if (entity instanceof LivingEntity) {
			LivingEntity le = (LivingEntity) entity;
			le.setAir(MathHelper.clamp(le.getAir() + value, 0, le.getMaxAir()));
		}
	}
}
