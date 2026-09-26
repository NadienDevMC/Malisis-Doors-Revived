package nadiendev.malisisdoorsrevived.movement;

import nadiendev.malisiscorerevived.animation.Transformation;
import nadiendev.malisiscorerevived.animation.Translation;
import nadiendev.malisisdoorsrevived.blockentity.DoorBlockEntity;
import net.minecraft.world.phys.AABB;

/**
 * Each half of the door slides in a different direction.
 */
public class Sliding4WaysMovement implements IDoorMovement
{
	@Override
	public AABB getOpenBoundingBox(DoorBlockEntity be, boolean topBlock, BoundingBoxType type)
	{
		if (type == BoundingBoxType.COLLISION && !topBlock)
			return null;
		float x = 0, y = 0;
		if (topBlock != be.isHingeLeft())
			x = topBlock ? DOOR_WIDTH - 1 : 1 - DOOR_WIDTH;
		else
			y = topBlock ? 1 - DOOR_WIDTH : DOOR_WIDTH - 1;
		return IDoorMovement.getHalfBoundingBox().move(x, y, 0);
	}

	@Override
	public Transformation<?> getTransformation(DoorBlockEntity be, DoorPart part)
	{
		boolean topBlock = part == DoorPart.TOP;
		float dir = -1 + DOOR_WIDTH;
		float toX = 0;
		float toY = 0;
		if (be.isHingeLeft())
			dir = -dir;
		if (topBlock == be.isHingeLeft())
			toY = dir;
		else
			toX = dir;

		return new Translation(toX, toY, 0).reversed(be.getDoorState().isClosingOrClosed()).forTicks(be.getOpeningTime());
	}
}
