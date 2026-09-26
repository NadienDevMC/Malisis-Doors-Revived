package nadiendev.malisisdoorsrevived.datagen;

import nadiendev.malisisdoorsrevived.MalisisDoors;
import nadiendev.malisisdoorsrevived.datagen.providers.MDLangProvider;
import nadiendev.malisisdoorsrevived.datagen.providers.MDModelProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = MalisisDoors.MODID)
public class MDDataGen
{
	@SubscribeEvent
	public static void gatherData(GatherDataEvent.Client event)
	{
		event.createProvider(MDModelProvider::new);
		event.createProvider(MDLangProvider::new);
	}
}
