package nadiendev.malisisdoorsrevived.integracion.wthit;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IClientRegistrar;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IWailaClientPlugin;
import nadiendev.malisiscorerevived.multiblock.MultiBlockHelper;
import nadiendev.malisiscorerevived.multiblock.MultiBlockPartBlock;
import nadiendev.malisisdoorsrevived.block.MalisisDoorBlock;
import nadiendev.malisisdoorsrevived.block.MalisisFenceGateBlock;
import nadiendev.malisisdoorsrevived.block.MalisisTrapDoorBlock;
import nadiendev.malisisdoorsrevived.door.DoorAppearance;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class MalisisDoorsWthitPlugin implements IWailaClientPlugin
{
	@Override
	public void register(IClientRegistrar registrar)
	{
		IBlockComponentProvider animatedCopies = new IBlockComponentProvider()
		{
			@Override
			public BlockState getOverride(IBlockAccessor accessor, IPluginConfig config)
			{
				return DoorAppearance.originalState(accessor.getBlockState());
			}
		};
		registrar.override(animatedCopies, MalisisDoorBlock.class);
		registrar.override(animatedCopies, MalisisTrapDoorBlock.class);
		registrar.override(animatedCopies, MalisisFenceGateBlock.class);

		registrar.override(new IBlockComponentProvider()
		{
			@Override
			public BlockState getOverride(IBlockAccessor accessor, IPluginConfig config)
			{
				BlockPos origin = MultiBlockHelper.getOrigin(accessor.getWorld(), accessor.getPosition());
				return origin != null ? accessor.getWorld().getBlockState(origin) : null;
			}
		}, MultiBlockPartBlock.class);
	}
}
