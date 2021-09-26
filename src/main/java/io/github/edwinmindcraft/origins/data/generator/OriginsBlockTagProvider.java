package io.github.edwinmindcraft.origins.data.generator;

import io.github.apace100.origins.Origins;
import io.github.edwinmindcraft.origins.data.tag.OriginsBlockTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class OriginsBlockTagProvider extends BlockTagsProvider {
	public OriginsBlockTagProvider(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
		super(generator, Origins.MODID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		this.tag(OriginsBlockTags.COBWEBS).add(Blocks.COBWEB);
	}
}
