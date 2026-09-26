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
 * 3x3 double doors.
 *
 * @author Ordinastie
 */
public class Door3x3Block extends MultiBlockDoorBlock
{
	private static final float W = IDoorMovement.DOOR_WIDTH;
	private static final List<BlockPos> POSITIONS = new ArrayList<>();
	static
	{
		for (int x = -1; x <= 1; x++)
			for (int y = 0; y < 3; y++)
				POSITIONS.add(new BlockPos(x, y, 0));
	}

	public Door3x3Block(Properties properties, boolean metal, DoorAppearance appearance)
	{
		super(properties, new DoorDescriptor()	.setMovement(DoorRegistry.DOOR_3X3)
												.setSound(metal ? DoorRegistry.RUSTY_DOOR_SOUND : DoorRegistry.WOODEN_DOOR_SOUND)
												.setDoubleDoor(false)
												.setOpeningTime(15), appearance);
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
			return new AABB[] { new AABB(-1, 0, -.5F, W - 1, 3, 1), new AABB(2 - W, 0, -.5F, 2, 3, 1) };
		return new AABB[] { new AABB(-1, 0, 1 - W, 2, 3, 1) };
	}
}
