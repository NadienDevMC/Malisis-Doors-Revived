package nadiendev.malisisdoorsrevived.animated;

import java.util.Map;

import nadiendev.malisisdoorsrevived.door.DoorAppearance;
import nadiendev.malisisdoorsrevived.door.IDoorBlock;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
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
		Map<BlockState, BlockStateModel> models = event.getBakingResult().blockStateModels();
		for (AnimatedCopies.Copy copy : AnimatedCopies.getCopies())
		{
			if (!(copy.animated() instanceof IDoorBlock door) || !(door.getAppearance() instanceof DoorAppearance.Copy appearance))
				continue;
			for (BlockState state : copy.animated().getStateDefinition().getPossibleStates())
			{
				BlockStateModel model = models.get(appearance.copyState(state));
				if (model != null)
					models.put(state, model);
			}
		}
	}
}
