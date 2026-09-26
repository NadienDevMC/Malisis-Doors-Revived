package nadiendev.malisisdoorsrevived.block;

import java.util.ArrayList;
import java.util.List;

import nadiendev.malisisdoorsrevived.blockentity.MultiBlockDoorBlockEntity;
import nadiendev.malisisdoorsrevived.door.DoorAppearance;
import nadiendev.malisisdoorsrevived.door.DoorDescriptor;
import nadiendev.malisisdoorsrevived.door.DoorRegistry;
import nadiendev.malisisdoorsrevived.door.DoorState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/**
 * Hi-tech sliding door : 3 blocks wide and 2 blocks high, with two sliding panels and latches.
 *
 * @author Ordinastie
 */
public class HitechDoorBlock extends MultiBlockDoorBlock
{
	private static final List<BlockPos> POSITIONS = new ArrayList<>();
	static
	{
		for (int x = -1; x <= 1; x++)
			for (int y = 0; y < 2; y++)
				POSITIONS.add(new BlockPos(x, y, 0));
	}

	public HitechDoorBlock(Properties properties, DoorAppearance appearance)
	{
		super(properties, new DoorDescriptor()	.setMovement(DoorRegistry.HITECH_DOOR)
												.setSound(DoorRegistry.PNEUMATIC_SOUND)
												.setDoubleDoor(false)
												.setOpeningTime(21), appearance);
	}

	@Override
	protected List<BlockPos> getRelativePositions(BlockState state)
	{
		return POSITIONS;
	}

	@Override
	public AABB[] getBoundingBoxes(MultiBlockDoorBlockEntity be, BlockState state, boolean collision)
	{
		if (collision && (be.getDoorState() != DoorState.CLOSED))
			return null;
		return new AABB[] { new AABB(-1, 0, 0.375F, 2, 2, 0.625F) };
	}
}
