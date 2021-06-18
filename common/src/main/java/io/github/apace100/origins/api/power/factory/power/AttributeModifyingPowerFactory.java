package io.github.apace100.origins.api.power.factory.power;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.configuration.power.IValueModifyingPowerConfiguration;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Lazy;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public abstract class AttributeModifyingPowerFactory<T extends IValueModifyingPowerConfiguration> extends ValueModifyingPowerFactory<T> {
	public static <C extends IValueModifyingPowerConfiguration, F extends AttributeModifyingPowerFactory<C>> double apply(Entity entity, F type, double baseValue) {
		if (type.hasAttributeBacking()) return baseValue;
		return OriginComponent.modify(entity, type, baseValue);
	}

	private final Lazy<EntityAttribute> lazyAttribute;

	protected AttributeModifyingPowerFactory(Codec<T> codec) {
		this(codec, true);
	}

	protected AttributeModifyingPowerFactory(Codec<T> codec, boolean allowConditions) {
		super(codec, allowConditions);
		this.lazyAttribute = new Lazy<>(this::getAttribute);
	}

	public boolean hasAttributeBacking() {
		return this.lazyAttribute.get() != null;
	}

	private Optional<EntityAttributeInstance> getAttribute(PlayerEntity player) {
		return player.getAttributes().hasAttribute(this.lazyAttribute.get()) ? Optional.ofNullable(player.getAttributeInstance(this.lazyAttribute.get())) : Optional.empty();
	}

	private void add(List<EntityAttributeModifier> configuration, PlayerEntity player) {
		this.getAttribute(player).ifPresent(x -> configuration.stream().filter(mod -> !x.hasModifier(mod)).forEach(x::addTemporaryModifier));
	}

	private void remove(List<EntityAttributeModifier> configuration, PlayerEntity player) {
		this.getAttribute(player).ifPresent(x -> configuration.stream().filter(x::hasModifier).forEach(x::removeModifier));
	}

	@Override
	public boolean canTick(ConfiguredPower<T, ?> configuration, PlayerEntity player) {
		return this.hasAttributeBacking() && this.shouldCheckConditions();
	}

	@Override
	public void tick(ConfiguredPower<T, ?> configuration, PlayerEntity player) {
		if (this.canTick(configuration, player)) {
			if (configuration.isActive(player))
				this.add(configuration.getConfiguration().modifiers().getContent(), player);
			else
				this.remove(configuration.getConfiguration().modifiers().getContent(), player);
		}
	}

	@Override
	protected void onAdded(T configuration, PlayerEntity player) {
		if (this.hasAttributeBacking() && !this.shouldCheckConditions())
			this.add(configuration.modifiers().getContent(), player);
	}

	@Override
	protected void onRemoved(T configuration, PlayerEntity player) {
		this.remove(configuration.modifiers().getContent(), player);
	}

	@Override
	protected int tickInterval(T configuration, PlayerEntity player) {
		return 20;
	}

	@Nullable
	public abstract EntityAttribute getAttribute();
}
