package nadiendev.malisisdoorsrevived.animated;

import java.util.Map;

import nadiendev.malisisdoorsrevived.door.DoorAppearance;
import nadiendev.malisisdoorsrevived.door.IDoorBlock;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ModelEvent;

public class AnimatedCopiesClient
{
	public static void init(IEventBus modEventBus)
	{
		modEventBus.addListener(AnimatedCopiesClient::useOriginalModels);
	}

	private static void useOriginalModels(ModelEvent.ModifyBakingResult event)
	{
		Map<ModelResourceLocation, BakedModel> models = event.getModels();
		for (AnimatedCopies.Copy copy : AnimatedCopies.getCopies())
		{
			if (!(copy.animated() instanceof IDoorBlock door) || !(door.getAppearance() instanceof DoorAppearance.Copy appearance))
				continue;
			for (BlockState state : copy.animated().getStateDefinition().getPossibleStates())
			{
				BakedModel model = models.get(BlockModelShaper.stateToModelLocation(appearance.copyState(state)));
				if (model != null)
					models.put(BlockModelShaper.stateToModelLocation(state), model);
			}
		}
	}
}
