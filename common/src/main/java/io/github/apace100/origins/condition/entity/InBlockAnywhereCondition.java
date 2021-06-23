package io.github.apace100.origins.condition.entity;

import io.github.apace100.origins.api.power.factory.EntityCondition;
import io.github.apace100.origins.condition.configuration.InBlockAnywhereConfiguration;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class InBlockAnywhereCondition extends EntityCondition<InBlockAnywhereConfiguration> {

	public InBlockAnywhereCondition() {
		super(InBlockAnywhereConfiguration.CODEC);
	}

	@Override
	public boolean check(InBlockAnywhereConfiguration configuration, LivingEntity entity) {
		int stopAt = configuration.comparison().getOptimalStoppingPoint();
		int count = 0;
		Box box = entity.getBoundingBox();
		BlockPos blockPos = new BlockPos(box.minX + 0.001D, box.minY + 0.001D, box.minZ + 0.001D);
		BlockPos blockPos2 = new BlockPos(box.maxX - 0.001D, Math.min(box.maxY - 0.001D, entity.world.getHeight()), box.maxZ - 0.001D);
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		for (int i = blockPos.getX(); i <= blockPos2.getX() && count < stopAt; ++i) {
			for (int j = blockPos.getY(); j <= blockPos2.getY() && count < stopAt; ++j) {
				for (int k = blockPos.getZ(); k <= blockPos2.getZ() && count < stopAt; ++k) {
					mutable.set(i, j, k);
					if (configuration.blockCondition().check(new CachedBlockPosition(entity.world, mutable, false))) {
						count++;
					}
				}
			}
		}
		return configuration.comparison().check(count);
	}
}
