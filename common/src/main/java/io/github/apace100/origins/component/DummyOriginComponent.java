package io.github.apace100.origins.component;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.origin.Origin;
import io.github.apace100.origins.api.origin.OriginLayer;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.registry.ModOrigins;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class DummyOriginComponent implements OriginComponent {
	@Override
	public boolean hasOrigin(OriginLayer layer) {
		return false;
	}

	@Override
	public boolean hasAllOrigins() {
		return false;
	}

	@Override
	public Map<OriginLayer, Origin> getOrigins() {
		return ImmutableMap.of();
	}

	@Override
	public Origin getOrigin(OriginLayer layer) {
		return ModOrigins.EMPTY;
	}

	@Override
	public boolean hadOriginBefore() {
		return false;
	}

	@Override
	public boolean hasPower(ConfiguredPower<?, ?> powerType) {
		return false;
	}

	@Override
	public boolean hasPower(Identifier powerType) {
		return false;
	}

	@Override
	public @Nullable ConfiguredPower<?, ?> getPower(Identifier identifier) {
		return null;
	}

	@Override
	public List<ConfiguredPower<?, ?>> getPowers() {
		return ImmutableList.of();
	}

	@Override
	public <T extends IOriginsFeatureConfiguration, F extends PowerFactory<T>> List<ConfiguredPower<T, F>> getPowers(F factory) {
		return ImmutableList.of();
	}

	@Override
	public <T extends IOriginsFeatureConfiguration, F extends PowerFactory<T>> List<ConfiguredPower<T, F>> getPowers(F factory, boolean includeInactive) {
		return ImmutableList.of();
	}

	@Override
	public void setOrigin(OriginLayer layer, Origin origin) {

	}

	@Override
	public void serverTick() {

	}

	@Override
	public void readFromNbt(CompoundTag compoundTag) {

	}

	@Override
	public CompoundTag writeToNbt(CompoundTag compoundTag) {
		if (compoundTag == null) throw new NullPointerException();
		return compoundTag;
	}

	@Override
	public void applySyncPacket(PacketByteBuf buf) {

	}

	@Override
	public void sync() {

	}

	@Override
	@NotNull
	public <T> T getPowerData(Identifier power, Supplier<? extends T> builder) {
		return builder.get();
	}

	@Override
	@NotNull
	public <T> T getPowerData(ConfiguredPower<?, ?> power, Supplier<? extends T> builder) {
		return builder.get();
	}
}
