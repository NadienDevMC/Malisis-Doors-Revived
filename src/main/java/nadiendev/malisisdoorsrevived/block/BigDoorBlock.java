package nadiendev.malisisdoorsrevived.block;

import java.util.ArrayList;
import java.util.List;

import nadiendev.malisisdoorsrevived.blockentity.MultiBlockDoorBlockEntity;
import nadiendev.malisisdoorsrevived.door.DoorAppearance;
import nadiendev.malisisdoorsrevived.door.DoorDescriptor;
import nadiendev.malisisdoorsrevived.door.DoorRegistry;
import nadiendev.malisisdoorsrevived.movement.IDoorMovement;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/**
 * Carriage and medieval doors : 4 blocks wide and 5 blocks high doors with a frame made of any block.
 *
 * @author Ordinastie
 */
public class BigDoorBlock extends MultiBlockDoorBlock
{
	private static final float W = IDoorMovement.DOOR_WIDTH;
	private static final List<BlockPos> POSITIONS = new ArrayList<>();
	static
	{
		for (int x = 0; x < 4; x++)
			for (int y = 0; y < 5; y++)
				POSITIONS.add(new BlockPos(x, y, 0));
	}

	public BigDoorBlock(Properties properties, DoorAppearance appearance)
	{
		super(properties, new DoorDescriptor()	.setMovement(DoorRegistry.CARRIAGE_DOOR)
												.setSound(DoorRegistry.CARRIAGE_DOOR_SOUND)
												.setDoubleDoor(false)
												.setOpeningTime(20), appearance);
	}

	@Override
	protected List<BlockPos> getRelativePositions(BlockState state)
	{
		return POSITIONS;
	}

	@Override
	public AABB[] getBoundingBoxes(MultiBlockDoorBlockEntity be, BlockState state, boolean collision)
	{
		if (collision && (be.isOpened() || be.isMoving()))
			return new AABB[] { new AABB(0, 0, -0.5F, 0.5F, 4, 1), new AABB(3.5F, 0, -0.5F, 4, 4, 1), new AABB(0, 4, 1 - W, 4, 5, 1) };
		return new AABB[] { new AABB(0, 0, 1 - W, 4, 5, 1) };
	}
}
