package io.github.edwinmindcraft.origins.data;

import io.github.edwinmindcraft.origins.data.generator.OriginsBlockTagProvider;
import io.github.edwinmindcraft.origins.data.generator.OriginsPowerProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

public class OriginsData {
	public static void initialize() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(OriginsData::gatherData);
	}

	private static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
		if (event.includeServer()) {
			generator.addProvider(new OriginsBlockTagProvider(generator, existingFileHelper));
			generator.addProvider(new OriginsPowerProvider(generator, existingFileHelper));
		}
	}
}
