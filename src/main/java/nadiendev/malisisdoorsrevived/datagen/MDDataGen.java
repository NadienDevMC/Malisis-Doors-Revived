package nadiendev.malisisdoorsrevived.datagen;

import nadiendev.malisisdoorsrevived.MalisisDoors;
import nadiendev.malisisdoorsrevived.datagen.providers.MDBlockStateProvider;
import nadiendev.malisisdoorsrevived.datagen.providers.MDItemModelProvider;
import nadiendev.malisisdoorsrevived.datagen.providers.MDLangProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = MalisisDoors.MODID)
public class MDDataGen
{
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event)
	{
		DataGenerator generator = event.getGenerator();
		PackOutput output = generator.getPackOutput();
		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

		generator.addProvider(event.includeClient(), new MDBlockStateProvider(output, existingFileHelper));
		generator.addProvider(event.includeClient(), new MDItemModelProvider(output, existingFileHelper));
		generator.addProvider(event.includeClient(), new MDLangProvider(output));
	}
}
