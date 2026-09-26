package nadiendev.malisisdoorsrevived.integracion.jade;

import nadiendev.malisiscorerevived.multiblock.MultiBlockHelper;
import nadiendev.malisisdoorsrevived.door.DoorAppearance;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class MalisisDoorsJadePlugin implements IWailaPlugin
{
	@Override
	public void registerClient(IWailaClientRegistration registration)
	{
		registration.addRayTraceCallback((hit, accessor, original) -> {
			if (!(accessor instanceof BlockAccessor block))
				return accessor;

			Level level = block.getLevel();
			BlockPos origin = MultiBlockHelper.getOrigin(level, block.getPosition());
			if (origin != null)
				return registration.blockAccessor().from(block).blockState(level.getBlockState(origin)).blockEntity(level.getBlockEntity(origin)).build();

			BlockState state = DoorAppearance.originalState(block.getBlockState());
			return state != null ? registration.blockAccessor().from(block).blockState(state).build() : accessor;
		});
	}
}
