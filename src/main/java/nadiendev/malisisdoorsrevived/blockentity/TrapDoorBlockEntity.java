package nadiendev.malisisdoorsrevived.blockentity;

import nadiendev.malisisdoorsrevived.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Block entity of the animated trapdoors.
 *
 * @author Ordinastie
 */
public class TrapDoorBlockEntity extends DoorBlockEntity
{
	public TrapDoorBlockEntity(BlockPos pos, BlockState state)
	{
		super(ModBlockEntities.TRAPDOOR.get(), pos, state);
	}

	@Override
	public DoorBlockEntity getDoubleDoor()
	{
		return null;
	}
}
